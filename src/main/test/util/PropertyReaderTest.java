package util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PropertyReaderTest {
    private PropertyReader pr;

    @Before
    public void setUp() throws Exception {
        pr=new PropertyReader();
    }

    @Test
    public void getProperty() throws Exception {
        Assert.assertEquals("E:\\",pr.getProperty("root_path"));
    }

}
