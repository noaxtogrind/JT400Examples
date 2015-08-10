/*******************************************************************************
 * Copyright (c) 2015 Remain Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remain Software - initial API and implementation
 ******************************************************************************/
package com.remainsoftware.jt400.examples.member;

import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.io.IOException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400File;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.MemberList;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.SequentialFile;

public class ListSource {

	private AS400 fAS400;

	public ListSource(AS400 pAS400) {
		this.fAS400 = pAS400;
	}

	/**
	 * Example on how to use this API.
	 * 
	 * @param args
	 * @throws AS400Exception
	 * @throws AS400SecurityException
	 * @throws ErrorCompletingRequestException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ObjectDoesNotExistException
	 * @throws PropertyVetoException
	 */
	public static void main(String[] args) throws AS400Exception, AS400SecurityException,
			ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException,
			PropertyVetoException {

		// ListSource listSource = new ListSource(new AS400("system", "user", "password"));
		ListSource listSource = new ListSource(new AS400());
		MemberDescription[] members = listSource.listMembers("QGPL", "QRPGSRC");
		String memberAsString = listSource.getMemberAsString(members[0]);
		System.out.println(memberAsString);
		dispose(listSource);

	}

	private static void dispose(ListSource listSource) {
		listSource.dispose();
		closeAWT();
	}

	private static void closeAWT() {
		// gets rid of AWT after prompt of OS/400 system and password
		for (Window window : Frame.getWindows()) {
			window.dispose();
		}
	}

	/**
	 * Returns the contents of the member as a string.
	 * 
	 * @param memberDescription
	 * @return
	 * @throws AS400Exception
	 * @throws AS400SecurityException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	public String getMemberAsString(MemberDescription memberDescription) throws AS400Exception,
			AS400SecurityException, InterruptedException, IOException, PropertyVetoException {

		AS400File file = new SequentialFile(fAS400, memberDescription.getPath());
		StringBuilder result = new StringBuilder();
		file.setRecordFormat();
		listFieldDescriptions(file);
		Record[] records = file.readAll();
		for (Record record : records) {
			result.append(record.toString());
			result.append(System.lineSeparator());
		}
		return result.toString();
	}

	private void listFieldDescriptions(AS400File file) {
		System.out.println();
		System.err.println("Fields:");
		for (FieldDescription description : file.getRecordFormat().getFieldDescriptions()) {
			System.out.println(description.getFieldName());
		}
		System.out.println();
	}

	/**
	 * Close all resources.
	 */
	public void dispose() {
		fAS400.disconnectAllServices();
	}

	/**
	 * @param pLibrary
	 * @param pFile
	 * @return a list off all members in the specified file.
	 * 
	 * @throws AS400Exception
	 * @throws AS400SecurityException
	 * @throws ErrorCompletingRequestException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ObjectDoesNotExistException
	 */
	public MemberDescription[] listMembers(String pLibrary, String pFile) throws AS400Exception,
			AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException,
			ObjectDoesNotExistException {

		System.out.println();
		System.err.println("Members:");

		MemberList memberList = new MemberList(fAS400, "QGPL", "QRPGSRC");
		memberList.load();
		MemberDescription[] result = memberList.getMemberDescriptions();
		for (MemberDescription memberDescription : result) {
			System.out.println(memberDescription.getPath());
		}
		return result;
	}
}
