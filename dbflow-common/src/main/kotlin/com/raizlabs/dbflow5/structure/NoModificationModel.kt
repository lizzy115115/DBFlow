package com.raizlabs.dbflow5.structure

import com.raizlabs.dbflow5.KClass
import com.raizlabs.dbflow5.Transient
import com.raizlabs.dbflow5.adapter.RetrievalAdapter
import com.raizlabs.dbflow5.config.retrievalAdapter
import com.raizlabs.dbflow5.database.DatabaseWrapper
import com.raizlabs.dbflow5.kClass

/**
 * Description: A convenience class for [ReadOnlyModel].
 */
abstract class NoModificationModel : ReadOnlyModel {

    @Suppress("UNCHECKED_CAST")
    @delegate:Transient
    private val retrievalAdapter: RetrievalAdapter<NoModificationModel> by lazy { (this.kClass as KClass<NoModificationModel>).retrievalAdapter }

    override fun exists(wrapper: DatabaseWrapper): Boolean = retrievalAdapter.exists(this, wrapper)

    @Suppress("UNCHECKED_CAST")
    override fun <T> load(wrapper: DatabaseWrapper): T? = retrievalAdapter.load(this, wrapper) as T?

    /**
     * Gets thrown when an operation is not valid for the SQL View
     */
    internal class InvalidSqlViewOperationException(detailMessage: String) : RuntimeException(detailMessage)
}
