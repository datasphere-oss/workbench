/*
 * Apache License
 * 
 * Copyright (c) 2020 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huahui.datasphere.workbench.common.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Notebooks implements Serializable {

	private static final long serialVersionUID = 3121043504974717517L;
	private List<Notebook> noteBooks;

	
	
	/**
	 * Parameterized Constructor
	 * @param noteBooks
	 * 		The list of Notebook.
	 */
	public Notebooks(List<Notebook> noteBooks) {
		super();
		this.noteBooks = noteBooks;
	}
	
	/**
	 * Default Constructor
	 */
	public Notebooks() { 
		this(new ArrayList<Notebook>());
	}

	/**
	 * @return the noteBooks
	 */
	public List<Notebook> getNoteBooks() {
		return noteBooks;
	}

	/**
	 * @param noteBooks the noteBooks to set
	 */
	public void setNoteBooks(List<Notebook> noteBooks) {
		this.noteBooks = noteBooks;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((noteBooks == null) ? 0 : noteBooks.hashCode());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Notebooks)) {
			return false;
		}
		Notebooks other = (Notebooks) obj;
		if (noteBooks == null) {
			if (other.noteBooks != null) {
				return false;
			}
		} else if (!noteBooks.equals(other.noteBooks)) {
			return false;
		}
		return true;
	}
	
	
	
}