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

public class MetadataGroupFixtureTest {

	MetadataGroupFixture fixture;

	@BeforeMethod
	public void setUp() {
		fixture = new MetadataGroupFixture();

		ClientDataGroup topLevelDataGroup = createTopLevelDataGroup();

		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		topLevelDataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("testTitle", "a title"));
		// ClientDataGroup childReferences =
		// ClientDataGroup.withNameInData("childReferences");
		// ClientDataGroup childReference =
		// createChildReferenceWithRepeatIdRecordTypeAndRecordId("0",
		// "metadataGroup", "someRecordId", "0", "X");
		// childReferences.addChild(childReference);
		// topLevelDataGroup.addChild(childReferences);
		return topLevelDataGroup;
	}

	@Test
	public void testNumOfChildrenWithoutRecord() throws Exception {
		RecordHolder.setRecord(null);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoTopLevelDatagroup() {
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(null);
		RecordHolder.setRecord(record);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentFirstLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNumOfChildrenNoMatchingChildFirstLevel() {
		fixture.setChildNameInData("NOTtestTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testOneChildWithMatchingNameInDataFirstLevel() {
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}

	@Test
	public void testChildDataGroupDoesNotExist() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoMatchingChildSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("NOTtestTitle", "Not a title"));
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testOneChildWithMatchingNameInDataSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("testTitle", "A title"));
		ClientDataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		RecordHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}

	// @Test
	// public void testRepeatMinIsCorrectSecondChild() {
	// createAndAddSecondChild();
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someOtherRecordId");
	// assertEquals(fixture.getRepeatMin(), "1");
	// }
	//
	// @Test
	// public void testRepeatMaxWithoutRecord() throws Exception {
	// RecordHolder.setRecord(null);
	// assertEquals(fixture.getRepeatMax(), "not found");
	// }
	//
	// @Test
	// public void testNoMatchingChildForRepeatMax() {
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("NOTsomeRecordId");
	// assertEquals(fixture.getRepeatMax(), "not found");
	// }
	//
	// @Test
	// public void testRepeatMaxIsCorrect() {
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someRecordId");
	// assertEquals(fixture.getRepeatMax(), "X");
	// }
	//
	// @Test
	// public void testRepeatMaxIsCorrectSecondChild() {
	// createAndAddSecondChild();
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someOtherRecordId");
	// assertEquals(fixture.getRepeatMax(), "3");
	// }
	//
	// @Test
	// public void testMoreThanOneTestOnSameRecord() {
	// createAndAddSecondChild();
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someRecordId");
	// assertEquals(fixture.getRepeatMin(), "0");
	// assertEquals(fixture.getRepeatMax(), "X");
	//
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someOtherRecordId");
	// assertEquals(fixture.getRepeatMin(), "1");
	// assertEquals(fixture.getRepeatMax(), "3");
	// }
	//
	// @Test
	// public void testMoreThanOneTestOnSameRecordNoMatchSecondLink() {
	// createAndAddSecondChild();
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("someRecordId");
	// assertEquals(fixture.getRepeatMin(), "0");
	// assertEquals(fixture.getRepeatMax(), "X");
	//
	// fixture.setLinkedRecordType("metadataGroup");
	// fixture.setLinkedRecordId("NOTsomeOtherRecordId");
	// assertEquals(fixture.getRepeatMin(), "not found");
	// assertEquals(fixture.getRepeatMax(), "not found");
	// }
}
