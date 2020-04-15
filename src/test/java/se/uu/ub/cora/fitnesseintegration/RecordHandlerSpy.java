/*
 * Copyright 2020 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.fitnesseintegration;

public class RecordHandlerSpy implements RecordHandler {

	public boolean readRecordListWasCalled = false;
	public boolean searchRecordWasCalled = false;
	public String url;
	public String filter;
	public String authToken;
	public String jsonToReturn = "some json returned from spy";
	public boolean readRecordWasCalled = false;
	public String json;
	public StatusTypeSpy statusTypeReturned;
	public String createdId;
	public String token;

	@Override
	public ReadResponse readRecordList(String url, String authToken, String filter) {
		readRecordListWasCalled = true;
		this.url = url;
		this.filter = filter;
		this.authToken = authToken;

		statusTypeReturned = new StatusTypeSpy();
		return new ReadResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public ReadResponse readRecord(String url, String authToken) {
		this.url = url;
		this.authToken = authToken;
		readRecordWasCalled = true;
		statusTypeReturned = new StatusTypeSpy();
		return new ReadResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public ReadResponse searchRecord(String url, String authToken, String json) {
		searchRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		statusTypeReturned = new StatusTypeSpy();
		return new ReadResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public CreateResponse createRecord(String url, String authToken, String json) {
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		statusTypeReturned = new StatusTypeSpy();
		statusTypeReturned.statusCodeToReturn = 201;
		ReadResponse readResponse = new ReadResponse(statusTypeReturned, jsonToReturn);

		createdId = "someCreatedId";
		token = "someToken";
		return new CreateResponse(readResponse, createdId, token);
	}

}
