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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.List;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.json.parser.JsonObject;

public class PermissionComparerFixture extends ComparerFixture {

	private PermissionComparerFactory permissionComparerFactory;
	private String permissions;

	public PermissionComparerFixture() {
		super();
		permissionComparerFactory = DependencyProvider.getPermissionsComparerFactory();
	}

	public String testCheckPermissions() {
		DataRecord dataRecord = DataHolder.getRecord();
		PermissionComparer comparer = permissionComparerFactory.factor(dataRecord);

		JsonObject permissionObject = jsonHandler.parseStringAsObject(permissions);
		List<String> errorMessages = comparer.checkDataRecordContainsPermissions(permissionObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;

	}

	PermissionComparerFactory getPermissionComparerFactory() {
		return permissionComparerFactory;
	}

}
