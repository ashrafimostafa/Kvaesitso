package ir.mostafa.launcher.locations

import ir.mostafa.launcher.locations.providers.PluginLocation
import ir.mostafa.launcher.locations.providers.openstreetmaps.OsmLocation
import ir.mostafa.launcher.locations.providers.openstreetmaps.OsmLocationProvider
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.SearchableDeserializer
import ir.mostafa.launcher.search.SearchableRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val locationsModule = module {
    single<OsmLocationProvider> { OsmLocationProvider(androidContext(), get()) }
    single<LocationsRepository> { LocationsRepository(androidContext(), get(), get(), get()) }
    factory<SearchableRepository<Location>>(named<Location>()) { get<LocationsRepository>() }
    factory<SearchableDeserializer>(named(OsmLocation.DOMAIN)) { OsmLocationDeserializer(get()) }
    factory<SearchableDeserializer>(named(PluginLocation.DOMAIN)) { PluginLocationDeserializer(androidContext(), get()) }
}