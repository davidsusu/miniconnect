package hu.webarticum.miniconnect.transfer.lab.queue;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.channel.queue.QueueBlockSource;
import hu.webarticum.miniconnect.transfer.channel.queue.QueueBlockTarget;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.transfer.util.ByteString;

public class QueueTestMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream innerOut = new PipedOutputStream();
        InputStream innerIn = new PipedInputStream(innerOut);

        BlockTarget innerBlockTarget = new SingleStreamBlockTarget(innerOut);
        BlockSource innerBlockSource = new SingleStreamBlockSource(innerIn);
        
        try (QueueBlockTarget queueBlockTarget = QueueBlockTarget.open(innerBlockTarget, 2)) {
            try (QueueBlockSource queueBlockSource = QueueBlockSource.open(innerBlockSource, 2)) {
                queueBlockTarget.send(new Block(ByteString.wrap("alma körte".getBytes(StandardCharsets.UTF_8))));
                queueBlockTarget.send(new Block(ByteString.wrap("xxx yyy".getBytes(StandardCharsets.UTF_8))));
                queueBlockTarget.send(new Block(ByteString.wrap("lorem ipsum".getBytes(StandardCharsets.UTF_8))));

                System.out.println("Sent.");
                System.out.println("Sleep 1 second before fetch...");
                Thread.sleep(1000);
                
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
            }
        }
    }
    
}