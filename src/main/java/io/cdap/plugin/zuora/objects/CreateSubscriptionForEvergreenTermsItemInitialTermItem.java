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

import com.google.gson.annotations.SerializedName;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.zuora.restobjects.annotations.ObjectDefinition;
import io.cdap.plugin.zuora.restobjects.annotations.ObjectFieldDefinition;
import io.cdap.plugin.zuora.restobjects.objects.BaseObject;


import javax.annotation.Nullable;

/**
* Object name: CreateSubscriptionForEvergreenTermsItemInitialTermItem (CreateSubscriptionForEvergreenTermsItemInitialTermItem)
* Related objects: 
**/
@SuppressWarnings("unused")
@ObjectDefinition(
  Name = "CreateSubscriptionForEvergreenTermsItemInitialTermItem",
  ObjectType = ObjectDefinition.ObjectDefinitionType.NESTED
)
public class CreateSubscriptionForEvergreenTermsItemInitialTermItem extends BaseObject {
  /**
  * Name: period (period), Type: integer
  * Options (custom, update, select): false, false, false
  **/
  @Nullable
  @SerializedName("period")
  @ObjectFieldDefinition(FieldType = Schema.Type.INT)
  private Integer period;

  /**
  * Name: periodType (periodType), Type: string
  * Options (custom, update, select): false, false, false
  **/
  @Nullable
  @SerializedName("periodType")
  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  private String periodType;

  /**
  * Name: startDate (startDate), Type: string
  * Options (custom, update, select): false, false, false
  **/
  @Nullable
  @SerializedName("startDate")
  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  private String startDate;

  /**
  * Name: termType (termType), Type: string
  * Options (custom, update, select): false, false, false
  **/
  @Nullable
  @SerializedName("termType")
  @ObjectFieldDefinition(FieldType = Schema.Type.STRING)
  private String termType;


  @Override
  public void addFields() {
    addCustomField("period", period, Integer.class);
    addCustomField("periodType", periodType, String.class);
    addCustomField("startDate", startDate, String.class);
    addCustomField("termType", termType, String.class);
  }
}
