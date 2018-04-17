package com.raizlabs.dbflow5.query.property

import com.raizlabs.dbflow5.KClass
import com.raizlabs.dbflow5.adapter.ModelAdapter
import com.raizlabs.dbflow5.config.FlowManager
import com.raizlabs.dbflow5.converter.TypeConverter
import com.raizlabs.dbflow5.query.NameAlias
import com.raizlabs.dbflow5.query.Operator

/**
 * Description: Provides convenience methods for [TypeConverter] when constructing queries.
 *
 * @author Andrew Grosner (fuzz)
 */

class TypeConvertedProperty<T, V> : Property<V> {

    private var databaseProperty: TypeConvertedProperty<V, T>? = null

    private var convertToDB: Boolean = false

    private val getter: TypeConverterGetter

    override val operator: Operator<V>
        get() = Operator.op(nameAlias, getter.getTypeConverter(table), convertToDB)

    override val table: KClass<*>
        get() = super.table!!

    /**
     * Generated by the compiler, looks up the type converter based on [ModelAdapter] when needed.
     * This is so we can properly retrieve the type converter at any time.
     */
    interface TypeConverterGetter {

        fun getTypeConverter(modelClass: KClass<*>): TypeConverter<*, *>
    }

    constructor(table: KClass<*>, nameAlias: NameAlias,
                convertToDB: Boolean,
                getter: TypeConverterGetter) : super(table, nameAlias) {
        this.convertToDB = convertToDB
        this.getter = getter
    }

    constructor(table: KClass<*>, columnName: String,
                convertToDB: Boolean,
                getter: TypeConverterGetter) : super(table, columnName) {
        this.convertToDB = convertToDB
        this.getter = getter
    }

    override fun withTable(): TypeConvertedProperty<T, V> {
        val nameAlias = this.nameAlias
            .newBuilder()
            .withTable(FlowManager.getTableName(table))
            .build()
        return TypeConvertedProperty(this.table, nameAlias, this.convertToDB, this.getter)
    }

    /**
     * @return A new [Property] that corresponds to the inverted type of the [TypeConvertedProperty].
     * Provides a convenience for supplying type converted methods within the DataClass of the [TypeConverter]
     */
    fun invertProperty(): Property<T> = databaseProperty
        ?: TypeConvertedProperty<V, T>(table, nameAlias,
            !convertToDB, object : TypeConverterGetter {
            override fun getTypeConverter(modelClass: KClass<*>): TypeConverter<*, *> =
                getter.getTypeConverter(modelClass)
        }).also { databaseProperty = it }

    override fun withTable(tableNameAlias: NameAlias): TypeConvertedProperty<T, V> {
        val nameAlias = this.nameAlias
            .newBuilder()
            .withTable(tableNameAlias.tableName)
            .build()
        return TypeConvertedProperty(this.table, nameAlias, this.convertToDB, this.getter)
    }
}
