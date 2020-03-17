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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

//{
//	  "children": [
//	    {"type" : "atomic",
//	      "name": "workoutName",
//	      "value": "cirkelfys"
//	    },
//	    {"type" : "atomic",
//	      "name": "instructorId",
//	      "value": "789"
//	    },
//	    {"type" : "atomic",
//	      "name": "numOfParticipants",
//	      "value": "30"
//	    },
//	    {"type" : "group",
//	      "name": "instructorName",
//	      "children":[
//	        {"type" : "atomic",
//	      "name": "firstName",
//	      "value": "Anna"
//	    },
//	    {"type" : "atomic",
//	      "name": "lastName",
//	      "value": "Ledare"
//	    }]
//	    },
//	    {"type" : "group",
//	      "name": "popularity",
//	      "children":[
//	        {"type" : "atomic",
//	      "name": "rating",
//	      "value": "8"
//	    }]
//	    }
//	  ]
//	}
public class ChildComparerValuesTest {

	private ChildComparerValues childComparer;
	private JsonParser jsonParser;
	private ClientDataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		childComparer = new ChildComparerValuesImp();
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("workoutName", "cirkelfys"));
		jsonParser = new OrgJsonParser();

	}

	@Test
	public void testCheckContainOKWhenOneAtomicChild() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());

	}

	@Test
	public void testCheckContainNotOKWhenChildValueDiffers() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"NOTcirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData workoutName does not have the correct value.");

	}

	@Test
	public void testCheckContainOKWhenOneAtomicChildAndOneGroupChild() {
		ClientDataGroup instructorName = ClientDataGroup.withNameInData("instructorName");
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("firstName", "Anna"));
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"type\":\"group\",\"name\":\"instructorName\",\"children\":[{\"type\":\"atomic\",\"name\":\"firstName\",\"value\":\"Anna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testNotOKWhenOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = ClientDataGroup.withNameInData("instructorName");
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("firstName", "Anna"));
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"type\":\"group\",\"name\":\"instructorName\",\"children\":[{\"type\":\"atomic\",\"name\":\"firstName\",\"value\":\"NOTAnna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName does not have the correct value.");
	}

	@Test
	public void testNotOKWhenOneGrandChildIsMissingOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = ClientDataGroup.withNameInData("instructorName");
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("firstName", "Anna"));
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("lastName", "Ledare"));
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"type\":\"group\",\"name\":\"instructorName\",\"children\":[{\"type\":\"atomic\",\"name\":\"firstName\",\"value\":\"NOTAnna\"},{\"type\":\"atomic\",\"name\":\"NOTlastName\",\"value\":\"Ledare\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName does not have the correct value.");
		assertEquals(errorMessages.get(1), "Child with nameInData NOTlastName is missing.");
	}

	// @Test
	// public void testContainOKWhenOneChild() {
	// JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertTrue(errorMessages.isEmpty());
	//
	// }
	//
	// @Test
	// public void testCheckContainOKWhenMoreThanOneChild() {
	// dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("instructorId", "45"));
	//
	// JsonValue jsonValue = jsonParser.parseString(
	// "{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
	// boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
	// assertTrue(containsChildren);
	//
	// }
	//
	// @Test
	// public void testContainOKWhenMoreThanOneChild() {
	// dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("instructorId", "45"));
	// JsonValue jsonValue = jsonParser.parseString(
	// "{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertTrue(errorMessages.isEmpty());
	// }
	//
	// @Test
	// public void testCheckContainNotOKNoChildExistInDataGroup() {
	// dataGroup = ClientDataGroup.withNameInData("someDataGroup");
	// JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertEquals(errorMessages.size(), 1);
	// assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");
	//
	// boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
	// assertFalse(containsChildren);
	// }
	//
	// @Test
	// public void testContainNotOKNoChildExistInDataGroup() {
	// dataGroup = ClientDataGroup.withNameInData("someDataGroup");
	// JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertEquals(errorMessages.size(), 1);
	// assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");
	//
	// }
	//
	// @Test
	// public void testCheckContainOneButNotTheOther() {
	// JsonValue jsonValue = jsonParser.parseString(
	// "{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
	// boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
	// assertFalse(containsChildren);
	//
	// }
	//
	// @Test
	// public void testContainOneButNotTheOther() {
	// JsonValue jsonValue = jsonParser.parseString(
	// "{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertEquals(errorMessages.size(), 1);
	// assertEquals(errorMessages.get(0), "Child with nameInData instructorId is missing.");
	//
	// }
	//
	// @Test
	// public void testContainNone() {
	// dataGroup = ClientDataGroup.withNameInData("someDataGroup");
	// JsonValue jsonValue = jsonParser.parseString(
	// "{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
	// List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
	// jsonValue);
	// assertEquals(errorMessages.size(), 2);
	// assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");
	// assertEquals(errorMessages.get(1), "Child with nameInData instructorId is missing.");
	//
	// }

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "child must contain key: name")
	public void testJsonValueDoesNotContainName() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"NOTname\":\"workoutName\"}]}");
		childComparer.checkDataGroupContainsChildrenWithValues(dataGroup, jsonValue);
	}
}
