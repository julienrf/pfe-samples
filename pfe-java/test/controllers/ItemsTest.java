package controllers;

import org.junit.Test;
import play.mvc.Result;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ItemsTest {
    @Test
    public void listItems() {
        Result response = callAction(routes.ref.Items.list());
        assertThat(status(response)).isEqualTo(OK);
        assertThat(contentAsString(response)).isEqualTo("[]");
    }
}
