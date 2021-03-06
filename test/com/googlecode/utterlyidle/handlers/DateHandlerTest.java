package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.Responses.response;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateHandlerTest {
    @Test
    public void addsDate() throws Exception {
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(response()), new StoppedClock(Dates.date(2000, 1, 1)));
        Response response = handler.handle(RequestBuilder.get("/foo").build());

        assertThat(header(response, DATE), is("Sat, 01 Jan 2000 00:00:00 UTC"));
    }

    @Test
    public void neverOverridesDate() throws Exception{
        DateHandler handler = new DateHandler(ReturnResponseHandler.returnsResponse(ResponseBuilder.response(Status.OK).header(DATE, "simon")), null);
        Response response = handler.handle(RequestBuilder.get("/foo").build());

        assertThat(header(response, DATE), is("simon"));
    }
}
