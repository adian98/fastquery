/*
 * Copyright (c) 2016-2017, fastquery.org and/or its affiliates. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For more information, please see http://www.fastquery.org/.
 * 
 */

package org.fastquery.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.fastquery.core.Placeholder;
import org.fastquery.util.TypeUtil;
import org.junit.Test;

/**
 * 测试正则
 * 
 * @author mei.sir@aliyun.cn
 */
public class PlaceholderTest {

	private static final Logger LOG = Logger.getLogger(PlaceholderTest.class);

	@Test
	public void placeholder() {
		// 匹配 (?4,?5,?6)的正则(允许有首尾空格)
		String reg1 = Placeholder.INV_REG;
		assertThat(Pattern.matches(reg1, "(?3,?7,?8)"), is(true));
		assertThat(Pattern.matches(reg1, "( ?3,?7 ,?8 ) "), is(true));
		assertThat(Pattern.matches(reg1, " ( ?3 ,?7 , ?8 )"), is(true));
		assertThat(Pattern.matches(reg1, "     (?3,   ?7,  ?8 )"), is(true));
		assertThat(Pattern.matches(reg1, " (?3, ?7,   ?8)"), is(true));
		assertThat(Pattern.matches(reg1, "( ?3,     ?7,?8)"), is(true));
		assertThat(Pattern.matches(reg1, "( ?3,?7, ?8 )      "), is(true));
		assertThat(Pattern.matches(reg1, "( ?3, ?7, ?8) "), is(true));

		assertThat(Pattern.matches(reg1, "( ?3, ?7 ?8) "), is(false));
		assertThat(Pattern.matches(reg1, "( ?3?7?8) "), is(false));
		assertThat(Pattern.matches(reg1, "( ?s,?7, ?8) "), is(false));
		assertThat(Pattern.matches(reg1, "( ?3, ?7, ?8)s "), is(false));
		assertThat(Pattern.matches(reg1, "(?3, ?7, ?8)12 "), is(false));

		assertThat(Pattern.matches(reg1, "(?3?7?8)"), is(false));
		assertThat(Pattern.matches(reg1, "( ?3666,?7 ?8 ) "), is(false));
		assertThat(Pattern.matches(reg1, " ( ?3777 32?7 , ?8 )"), is(false));
		assertThat(Pattern.matches(reg1, "     (?3xx,   ?7,  ?8 )"), is(false));
		assertThat(Pattern.matches(reg1, " (?3a, ?7,   ?8)"), is(false));
		assertThat(Pattern.matches(reg1, "( ?3,  263, ?7,?8)"), is(false));
		assertThat(Pattern.matches(reg1, "( ?3,?7, ?8,? )      "), is(false));
		assertThat(Pattern.matches(reg1, "( ?3, ?x5, ?8) "), is(false));

		// 不区分大小写匹配格式 "?8 and ?9"
		reg1 = Placeholder.ANDV_REG;
		assertThat(Pattern.matches(reg1, "?12 AnD ?456"), is(true));
		assertThat(Pattern.matches(reg1, "?1 AnD ?45"), is(true));
		assertThat(Pattern.matches(reg1, "?3 AnD ?6"), is(true));
		assertThat(Pattern.matches(reg1, "?3 AnD ?456"), is(true));
		assertThat(Pattern.matches(reg1, " ?123     AnD ?456 "), is(true));
		assertThat(Pattern.matches(reg1, "    ?123 AnD ?456         "), is(true));
		assertThat(Pattern.matches(reg1, "    ?123 AnD     ?456"), is(true));
		assertThat(Pattern.matches(reg1, "?123      AnD ?456"), is(true));
		assertThat(Pattern.matches(reg1, "?123 AnD        ?456 "), is(true));

		assertThat(Pattern.matches(reg1, "?12AnD ?456"), is(false));
		assertThat(Pattern.matches(reg1, "?1 AnD?45"), is(false));
		assertThat(Pattern.matches(reg1, "?3 AndD ?6"), is(false));
		assertThat(Pattern.matches(reg1, "?3 AnD ?45x"), is(false));
		assertThat(Pattern.matches(reg1, " ?123  AAnD ?456 "), is(false));
		assertThat(Pattern.matches(reg1, "    ? AnD ?456         "), is(false));
		assertThat(Pattern.matches(reg1, "    ?123 AnD     ?"), is(false));
		assertThat(Pattern.matches(reg1, "?123      AnND ?456"), is(false));
		assertThat(Pattern.matches(reg1, "? 123 AnD ?456 "), is(false));
	}

	@Test
	public void Q_MATCH() {
		String str = "select * from UserInfo where name like `- - ?_  --- -`   and age like `-_?-` and akjgew `-  ?_-` and sge`-  ?                     -`";

		List<String> ssms = TypeUtil.matches(str, Placeholder.SMILE);
		
		for (String string : ssms) {
			LOG.debug(string);
		}
		
		assertThat(ssms.size(), is(4));
 
		assertThat(ssms.get(0), equalTo("`- - ?_  --- -`"));

		assertThat(ssms.get(1), equalTo("`-_?-`"));

		assertThat(ssms.get(2), equalTo("`-  ?_-`"));
		
		assertThat(ssms.get(3), equalTo("`-  ?                     -`"));

		ssms.forEach(m -> LOG.debug(m));
	}
	
	@Test
	public void SL_REG1(){
		String str = ":a[]%:b% %:c%";
		List<String> ssms = TypeUtil.matches(str, Placeholder.COLON_REG);
		assertThat(ssms.size(), is(3));
		assertThat(ssms.get(0), equalTo(":a"));
		assertThat(ssms.get(1), equalTo(":b"));
		assertThat(ssms.get(2), equalTo(":c"));
	}
	
	
	@Test
	public void SL_REG2(){
		String str = "_:%aa_b  %:Ccc_d%:eeE:F_:1%%%%_jkjgwoxl:abc";
		List<String> ssms = TypeUtil.matches(str, Placeholder.COLON_REG);
		assertThat(ssms.size(), is(5));
		assertThat(ssms.get(0), equalTo(":Ccc"));
		assertThat(ssms.get(1), equalTo(":eeE"));
		assertThat(ssms.get(2), equalTo(":F"));
		assertThat(ssms.get(3), equalTo(":1"));
		assertThat(ssms.get(4), equalTo(":abc"));
	}
	
	@Test
	public void SL_REG3() {
		String str = ":id,:name,:age";
		List<String> ssms = TypeUtil.matches(str, Placeholder.COLON_REG);
		assertThat(ssms.size(), is(3));
		assertThat(ssms.get(0), equalTo(":id"));
		assertThat(ssms.get(1), equalTo(":name"));
		assertThat(ssms.get(2), equalTo(":age"));
	}
	
	@Test
	public void PERCENT(){
		String str = "";
		boolean b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(false));
		
		str = "%";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(true));
		
		str = "a%";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(false));
		
		str = "%aa";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(false));
		
		str = "%%";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(true));
		
		str = "%%%";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(true));
		
		str = "%%%";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(true));
		
		str = "%%%a";
		b = Pattern.matches(Placeholder.PERCENT, str);
		assertThat(b, is(false));
	}
	
	@Test
	public void SEARCH_NUM(){
		String s = "`-%?103%?1kjgw?2?398klgw?3-`";
		List<String> strs = TypeUtil.matches(s, Placeholder.SEARCH_NUM);
		for (String str : strs) {
			assertThat(Pattern.matches("\\d+", str), is(true));
		}
	}
	
	@Test
	public void EL_OR_COLON() {
		String str = "jklgjw ,gwlljlgw `- :name_ :info- $name_ $ok.ax -` and ${abc_} conm ${mark} orfkljgw xg ${a} lgwiouo$_iwk$ jhlkgikw $abc}";
		List<String> strs = TypeUtil.matches(str,Placeholder.EL_OR_COLON);
		assertThat(strs.size(), is(8));
		assertThat(strs.get(0), equalTo(":name"));
		assertThat(strs.get(1), equalTo(":info"));
		assertThat(strs.get(2), equalTo("$name"));
		assertThat(strs.get(3), equalTo("$ok"));
		assertThat(strs.get(4), equalTo("${abc"));
		assertThat(strs.get(5), equalTo("${mark}"));
		assertThat(strs.get(6), equalTo("${a}"));
		assertThat(strs.get(7), equalTo("$abc}"));
	}
	
}


































