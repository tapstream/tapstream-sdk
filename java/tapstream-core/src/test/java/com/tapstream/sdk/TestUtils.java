package com.tapstream.sdk;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    @Test
    public void testReadFully() throws Exception {
        String expectedContents = "the stream contents";
        ByteArrayInputStream stream = new ByteArrayInputStream(expectedContents.getBytes());
        byte[] actualBytes = Utils.readFully(stream);
        assertThat(actualBytes, is(expectedContents.getBytes()));
    }


    interface TestCloseable extends Closeable {
        boolean wasClosed();
    }

    @Test
    public void testCloseQuietly() throws Exception {

        TestCloseable wellBehaved = new TestCloseable(){

            boolean closed = false;

            @Override
            public boolean wasClosed() {
                return closed;
            }

            @Override
            public void close() throws IOException {
                closed = true;
            }
        };

        TestCloseable hasErrors = new TestCloseable(){

            boolean closed = false;

            @Override
            public boolean wasClosed() {
                return closed;
            }

            @Override
            public void close() throws IOException {
                closed = true;
                throw new IOException("The error");
            }
        };

        Utils.closeQuietly(wellBehaved);
        assertThat(wellBehaved.wasClosed(), is(true));

        Utils.closeQuietly(hasErrors);
        assertThat(wellBehaved.wasClosed(), is(true));
    }

    @Test
    public void testGetOrDefault() throws Exception {
        assertThat(Utils.getOrDefault(null, "default"), is("default"));
        assertThat(Utils.getOrDefault("valid", "default"), is("valid"));
    }
}
