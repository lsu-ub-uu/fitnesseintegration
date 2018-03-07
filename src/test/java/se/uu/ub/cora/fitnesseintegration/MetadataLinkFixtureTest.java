/*
 * Copyright 2018 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class MetadataLinkFixtureTest {

	MetadataLinkFixture fixture;

	@BeforeMethod
	public void setUp() {
		fixture = new MetadataLinkFixture();

		ClientDataGroup topLevelDataGroup = createTopLevelDataGroup();

		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("metadata");
		ClientDataGroup childReferences = ClientDataGroup.withNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("0",
				"metadataGroup", "someRecordId", "0", "X");
		childReferences.addChild(childReference);
		topLevelDataGroup.addChild(childReferences);
		return topLevelDataGroup;
	}

	private ClientDataGroup createChildReferenceWithRepeatIdRecordTypeAndRecordId(String repeatId,
			String linkedRecordType, String linkedRecordId, String repeatMin, String repeatMax) {
		ClientDataGroup childReference = ClientDataGroup.withNameInData("childReference");
		childReference.setRepeatId(repeatId);
		ClientDataGroup ref = ClientDataGroup.withNameInData("ref");
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		childReference.addChild(ref);
		childReference.addChild(ClientDataAtomic.withNameInDataAndValue("repeatMin", repeatMin));
		childReference.addChild(ClientDataAtomic.withNameInDataAndValue("repeatMax", repeatMax));
		return childReference;
	}

	@Test
	public void testLinkIsNotPresent() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("metadata");
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testNoTopLevelDatagroup() {
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(null);
		RecordHolder.setRecord(record);
		fixture.setLinkedRecordId("someRecordId");
		fixture.setLinkedRecordType("metadataGroup");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	private void createAndAddSecondChild() {
		ClientDataRecord record = RecordHolder.getRecord();
		ClientDataGroup clientDataGroup = record.getClientDataGroup();
		ClientDataGroup childReferences = clientDataGroup
				.getFirstGroupWithNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("1",
				"metadataGroup", "someOtherRecordId", "1", "3");
		childReferences.addChild(childReference);
	}

	@Test
	public void testRepeatMinWithoutRecord() throws Exception {
		RecordHolder.setRecord(null);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMinRecordType() {
		fixture.setLinkedRecordType("NOTmetadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMin() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testRepeatMinIsCorrect() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
	}

	@Test
	public void testRepeatMinIsCorrectSecondChild() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "1");
	}

	@Test
	public void testRepeatMaxWithoutRecord() throws Exception {
		RecordHolder.setRecord(null);
		assertEquals(fixture.getRepeatMax(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMax() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeRecordId");
		assertEquals(fixture.getRepeatMax(), "not found");
	}

	@Test
	public void testRepeatMaxIsCorrect() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMax(), "X");
	}

	@Test
	public void testRepeatMaxIsCorrectSecondChild() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMax(), "3");
	}

	@Test
	public void testMoreThanOneTestOnSameRecord() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
		assertEquals(fixture.getRepeatMax(), "X");

		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "1");
		assertEquals(fixture.getRepeatMax(), "3");
	}

	@Test
	public void testMoreThanOneTestOnSameRecordNoMatchSecondLink() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
		assertEquals(fixture.getRepeatMax(), "X");

		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
		assertEquals(fixture.getRepeatMax(), "not found");
	}
}
