/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.openwise.redis.clientlist;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @author Petr Juza
 * @since 2.0
 */
public class Connection {

    // id=22199808 addr=80.95.99.142:38150 fd=120 name= age=3 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=del

    public String id;
    public String addr;
    public String fd;
    public String name;
    public int age;
    public int idle;
    public String flags;
    public String db;
    public String sub;
    public String psub;
    public int multi;
    public int qbuf;
    public int qbufFree;
    public int obl;
    public int oll;
    public int omem;
    public String events;
    public String cmd;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("id", leftPadStr(id, 10))
                .append("addr", leftPadStr(addr, 23))
                .append("fd", leftPadStr(fd, 6))
                .append("name", leftPadStr(StringUtils.substring(name, 15), 15))
                .append("age", leftPadInt(age, 10))
                .append("idle", leftPadInt(idle, 5))
                .append("flags", leftPadStr(flags, 5))
                .append("db", leftPadStr(db, 3))
                .append("sub", leftPadStr(sub, 3))
                .append("psub", leftPadStr(psub, 3))
                .append("multi", leftPadInt(multi, 3))
                .append("qbuf", leftPadInt(qbuf, 7))
                .append("qbufFree", leftPadInt(qbufFree, 7))
                .append("obl", leftPadInt(obl, 5))
                .append("oll", leftPadInt(oll, 5))
                .append("omem", leftPadInt(omem, 5))
                .append("events", leftPadStr(events, 5))
                .append("cmd", leftPadStr(cmd, 10))
                .toString();
    }

    private String leftPadStr(String str, int pad) {
        return StringUtils.leftPad(str, pad);
    }

    private String leftPadInt(Integer str, int pad) {
        return StringUtils.leftPad(str.toString(), pad);
    }
}
