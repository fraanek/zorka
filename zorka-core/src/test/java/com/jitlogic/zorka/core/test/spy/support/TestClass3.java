/*
 * Copyright 2012-2020 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jitlogic.zorka.core.test.spy.support;

import java.io.IOException;

public class TestClass3 {

    private int calls = 0;
    private int catches = 0;
    private int finals = 0;


    private void errorGen(Boolean b) throws IOException {
        if (b) {
            throw new IOException("oja!");
        }
    }

    public void tryCatchFinally0(Boolean b) {
        try {
            calls++;
            errorGen(b);
        } catch (IOException e) {
            catches++;
        }
    }



    public void tryCatchFinally1(Boolean b) {
        try {
            calls++;

            try {
                errorGen(b);
            } catch (IOException e) {
                catches++;
            }
        } finally {
            finals++;
        }
    }


    public void tryCatchFinally2(Boolean b) {
        try {
            try {
                calls++;
                errorGen(b);
            } catch (IOException e) {
                catches++;
            }
        } catch (Exception e) {
            finals++;
        }
    }

}
