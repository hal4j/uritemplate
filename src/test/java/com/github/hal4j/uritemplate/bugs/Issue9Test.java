package com.github.hal4j.uritemplate.bugs;

import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Issue9Test {

    @Test
    public void shouldEncodeSimpleValueWithCyrillicCharacters() {
        String s = new URITemplate("http://www.example.com/api{?quote:3}")
                .expand("quote", "Восемьдесят три процента всех дней в году начинаются одинаково: звенит будильник.")
                .toString();
        assertEquals("http://www.example.com/api?quote=%D0%92%D0%BE%D1%81", s);
    }

    @Test
    public void shouldNotEncodePlusModifierWithCyrillicCharacters() {
        String s = new URITemplate("http://www.example.com/api?quote={+quote:3}")
                .expand("quote", "Восемьдесят три процента всех дней в году начинаются одинаково: звенит будильник.")
                .toString();
        assertEquals("http://www.example.com/api?quote=Вос", s);
    }

}
