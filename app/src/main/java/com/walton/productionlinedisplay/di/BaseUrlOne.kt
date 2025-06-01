// File: Qualifiers.kt
package com.walton.productionlinedisplay.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlOne

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlTwo
