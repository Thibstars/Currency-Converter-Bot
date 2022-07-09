/*
 * Copyright (c) 2022 Thibault Helsmoortel.
 *
 *  This file is part of Currency Converter Bot.
 *
 *  Currency Converter Bot is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Currency Converter Bot is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Currency Converter Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.thibaulthelsmoortel.currencyconverterbot.client;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

/**
 * @author Thibault Helsmoortel
 */
@Getter
public abstract class ClientBaseTest extends BaseTest {

    @Qualifier("apiClient")
    @MockBean
    private WebClient apiClient;

    @MockBean
    private Consumer<HttpHeaders> apiHeaders;

    @Captor
    private ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor;

    public URI getUri() {
        return getUriFunctionCaptor().getValue().apply(new DefaultUriBuilderFactory().builder());
    }

    public void assertPathEquals(String path) {
        Assertions.assertEquals(
                path,
                getUri().getPath(),
                "Called path must be correct."
        );
    }

    public void assertParamValueEquals(String paramName, String paramValue) {
        Assertions.assertTrue(URLEncodedUtils.parse(getUri(), StandardCharsets.UTF_8).stream()
                        .filter(param -> param.getName().equals(paramName))
                        .anyMatch(param -> param.getValue().equals(paramValue)),
                "Could not find matching value for parameter: " + paramName);
    }

}
