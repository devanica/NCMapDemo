package com.netcheck.ncmapdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model class used in Room implementation architecture.
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  02.18.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 */
@Entity // if table name is not specified, name of the table == name of the class (Test).
class Test {

    @PrimaryKey(autoGenerate = true)
    var id = 0
    var dateTime: String? = null
    var technology: String? = null
    var type: String? = null
    var place: String? = null
    var lat: Double? = null
    var lng: Double? = null

}