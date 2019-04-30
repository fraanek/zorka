package com.jitlogic.zorka.core.test.spy;

import com.jitlogic.zorka.common.ZorkaSubmitter;
import com.jitlogic.zorka.common.tracedata.DTraceContext;
import com.jitlogic.zorka.common.tracedata.SymbolicRecord;
import com.jitlogic.zorka.common.tracedata.TraceMarker;
import com.jitlogic.zorka.common.tracedata.TraceRecord;
import com.jitlogic.zorka.common.util.JSONReader;
import com.jitlogic.zorka.core.spy.TracerLib;
import com.jitlogic.zorka.core.spy.output.DTraceFormatterZJ;
import com.jitlogic.zorka.core.spy.output.DTraceOutput;
import com.jitlogic.zorka.core.test.support.ZorkaFixture;

import org.junit.Test;

import static com.jitlogic.zorka.core.spy.TracerLib.DFK_SERVER;
import static com.jitlogic.zorka.core.spy.TracerLib.DFM_ZIPKIN;
import static org.junit.Assert.*;
import static com.jitlogic.zorka.common.util.ObjectInspector.get;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests all
 */
public class DTraceOutputsUnitTest extends ZorkaFixture {

    private List<byte[]> results = new ArrayList<byte[]>();

    private ZorkaSubmitter<byte[]> rawOutput = new ZorkaSubmitter<byte[]>() {
        @Override
        public boolean submit(byte[] item) {
            results.add(item);
            return true;
        }
    };


    private TraceRecord tr1() {
        TraceRecord tr = tr("some.Class", "someMethod", "()V",
                10, 1, 0, 10000L);
        TraceMarker tm = new TraceMarker(tr, sid("HTTP"), 142000000L);
        DTraceContext ds = new DTraceContext(42L, 24L, 44L, 64L,
                14200000L, DFM_ZIPKIN|DFK_SERVER);
        tm.setDstate(ds);
        tr.setMarker(tm);

        tr.setAttr(sid("REMOTE_IP"), "1.2.3.4");
        tr.setAttr(sid("REMOTE_PORT"), "80");
        tr.setAttr(sid("LOCAL_IP"), "5.6.7.8");
        tr.setAttr(sid("LOCAL_PORT"), 8080);

        tr.setAttr(sid("URL"), "/test.jsp");
        tr.setAttr(sid("METHOD"), "GET");
        tr.setAttr(sid("STATUS"), 200);

        return tr;
    }


    @Test
    public void testSingleZipkinJsonMsg() throws Exception {
        TraceRecord tr = tr1();

        ZorkaSubmitter<SymbolicRecord> out = new DTraceOutput(
                new DTraceFormatterZJ(config, symbols, TracerLib.OPENTRACING_TAGS),
                rawOutput);

        out.submit(tr);

        assertEquals(1, results.size());

        Object json = new JSONReader().read(new String(results.get(0)));

        System.out.println(json);

        assertEquals(tr.getMarker().getDstate().getTraceIdHex(), get(json, "0", "traceId"));
        assertEquals(tr.getMarker().getDstate().getSpanIdHex(), get(json, "0", "id"));
        assertEquals("SERVER", get(json, "0", "kind"));
        assertEquals("1.2.3.4", get(json, "0", "remoteEndpoint", "ipv4"));
        assertEquals(80L, get(json, "0", "remoteEndpoint", "port"));

    }


}
