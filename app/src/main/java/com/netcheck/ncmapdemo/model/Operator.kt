package com.netcheck.ncmapdemo.model

/**
 * Operator is a model class used when getting operator array from server on tile click in map fragment.
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  22.01.2021. </dd>
 * </dl>
 ******************************************************************************
 *
 */
data class Operator(
        var id: Int,
        val iso: String,
        val name: String)