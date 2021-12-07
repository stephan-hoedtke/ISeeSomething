package com.stho.isee.storage

interface IStorage {
    fun readSecrets(): List<Secret>
    fun save(secret: Secret)
}