package net.ktor.dao

import net.ktor.model.Address
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable

interface DAOFacade: Closeable{
    fun init()
    fun createAddress(name:String, email:String, city:String)
    fun updateAddress(id:Int, name:String, email:String, city:String)
    fun deleteAddress(id:Int)
    fun getAddress(id:Int): Address?
    fun getAllAddresses(): List<Address>
}

class DAOFacadeDatabase(val db: Database): DAOFacade{

    override fun init() =
        transaction(db) {
            SchemaUtils.create(Addresses)
        }

    override fun createAddress(name: String, email: String, city: String) =
        transaction(db) {
            Addresses.insert {it[Addresses.name] = name;
                it[Addresses.email] = email; it[Addresses.city] = city;
            }
            Unit
        }

    override fun updateAddress(id: Int, name: String, email: String, city: String) =
        transaction(db) {
            Addresses.update({Addresses.id eq id}){
                it[Addresses.name] = name
                it[Addresses.email] = email
                it[Addresses.city] = city
            }
            Unit
        }

    override fun deleteAddress(id: Int) = transaction(db) {
        Addresses.deleteWhere { Addresses.id eq id }
        Unit
    }


    override fun getAddress(id: Int) =
        transaction(db) {
            Addresses.select { Addresses.id eq id }.map {
                Address(it[Addresses.id], it[Addresses.name], it[Addresses.email], it[Addresses.city]
                )
            }.singleOrNull()
        }

    override fun getAllAddresses() = transaction(db) {
        Addresses.selectAll().map {
            Address(it[Addresses.id], it[Addresses.name], it[Addresses.email], it[Addresses.city]
            )
        }
    }

    override fun close() { }
}