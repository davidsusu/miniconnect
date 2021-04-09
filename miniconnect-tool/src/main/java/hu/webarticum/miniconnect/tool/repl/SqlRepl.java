package hu.webarticum.miniconnect.tool.repl;

import java.io.IOException;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniResult;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {

    private static final Pattern HELP_PATTERN = Pattern.compile(
            "\\s*help\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?", Pattern.CASE_INSENSITIVE);

    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?", Pattern.CASE_INSENSITIVE);

    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "^(?:" + HELP_PATTERN + "|" + QUIT_PATTERN +
                    "|(?:[^'\"`\\\\;]++|\\\\.|(['\"`])(?:(?:\\\\|\\1)\\1|(?!\\1).)++\\1)*;.*+)$",
            Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);


    private final MiniSession session;

    private final Appendable out;


    public SqlRepl(MiniSession session, Appendable out) {
        this.session = session;
        this.out = out;
    }


    @Override
    public Pattern commandPattern() {
        return COMMAND_PATTERN;
    }

    @Override
    public void welcome() throws IOException {
        out.append("\nWelcome in miniConnect SQL REPL!\n\n");
    }

    @Override
    public void prompt() throws IOException {
        out.append("SQL > ");
    }

    @Override
    public void prompt2() throws IOException {
        out.append("    > ");
    }

    @Override
    public boolean execute(String command) throws IOException {
        if (HELP_PATTERN.matcher(command).matches()) {
            help();
            return true;
        }

        if (QUIT_PATTERN.matcher(command).matches()) {
            return false;
        }

        MiniResult result = null;
        try {
            result = session.execute(command);
        } catch (Exception e) {
            printException(e);
        }

        if (result != null) {
            printResult(result);
        }

        return true;
    }

    private void printException(Exception e) throws IOException {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = e.getClass().getName();
        }
        out.append(message);
        out.append('\n');
    }

    private void printResult(MiniResult result) throws IOException {
        if (!result.success()) {
            out.append(String.format(
                    "ERROR(%s): %s%n",
                    result.errorCode(),
                    result.errorMessage()));
            return;
        }

        new ResultSetPrinter().print(result.resultSet(), out);
    }

    private void help() throws IOException {
        out.append('\n');
        out.append(String.format("  MiniConnect SQL REPL - %s%n",
                session.getClass().getSimpleName()));
        out.append('\n');
        out.append("  Commands:\n");
        out.append("    \"help\": prints this document\n");
        out.append("    \"exit\", \"quit\": quits this program\n");
        out.append("    <any SQL>: will be executed in the session\n");
        out.append("      (must be terminated with \";\")\n");
        out.append('\n');
    }

    @Override
    public void bye() throws IOException {
        out.append("\nBye-bye!\n\n");
    }

}