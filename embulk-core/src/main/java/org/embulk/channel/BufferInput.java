package org.embulk.channel;

import java.util.Iterator;
import org.embulk.buffer.Buffer;

public class BufferInput
        implements Iterable<Buffer>
{
    protected final DataChannel<Buffer> channel;

    protected BufferInput(DataChannel<Buffer> channel)
    {
        this.channel = channel;
    }

    @Override
    public Iterator<Buffer> iterator()
    {
        return channel.iterator();
    }
}