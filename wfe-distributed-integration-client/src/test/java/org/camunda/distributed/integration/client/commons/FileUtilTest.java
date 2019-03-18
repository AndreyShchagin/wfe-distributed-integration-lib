package org.camunda.distributed.integration.client.commons;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.is;

public class FileUtilTest {

    @Test
    public void replaceSeparatorWithOSSeparator() {
        if(File.separator.equals(FileUtil.FILE_SEPARATOR_SLASH))
            Assert.assertThat(FileUtil.replaceSeparatorWithOSSeparator("a\\b"), is("a/b"));
        else
            Assert.assertThat(FileUtil.replaceSeparatorWithOSSeparator("a/b"), is("a\\b"));
    }

    @Test
    public void copyInputStream() {
        InputStream is = new ByteArrayInputStream(new byte[]{1,2,3,4});
        OutputStream os = new ByteArrayOutputStream();
        FileUtil.copyInputStream(is,os);
        Assert.assertThat(((ByteArrayOutputStream) os).size(), is(4));
        Assert.assertArrayEquals(((ByteArrayOutputStream) os).toByteArray(), new byte[]{1,2,3,4});
    }
}