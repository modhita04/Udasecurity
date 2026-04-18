package com.udacity.catpoint.security.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StyleServiceTest {

    @Test
    void headingFontExists() {
        assertNotNull(StyleService.HEADING_FONT);
    }
}
