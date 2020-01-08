/*
 *  Copyright © 2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */
package io.cdap.plugin.zuora.objects;

import io.cdap.plugin.zuora.restobjects.annotations.ObjectDefinition;
import io.cdap.plugin.zuora.restobjects.objects.BaseObject;


/**
* Object name: AccountingCodeObjectCustomFields (AccountingCodeObjectCustomFields)
* Related objects:
**/
@SuppressWarnings("unused")
@ObjectDefinition(
  Name = "AccountingCodeObjectCustomFields",
  ObjectType = ObjectDefinition.ObjectDefinitionType.NESTED
)
public class AccountingCodeObjectCustomFields extends BaseObject {

  @Override
  public void addFields() {
  }
}