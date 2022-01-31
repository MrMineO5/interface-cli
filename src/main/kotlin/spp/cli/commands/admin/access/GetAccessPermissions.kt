/*
 * Source++, the open-source live coding platform.
 * Copyright (C) 2022 CodeBrig, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package spp.cli.commands.admin.access

import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.MapJsonWriter
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking
import spp.cli.Main
import spp.cli.PlatformCLI
import spp.cli.PlatformCLI.echoError
import spp.cli.protocol.access.GetAccessPermissionsQuery
import spp.cli.protocol.access.adapter.GetAccessPermissionsQuery_ResponseAdapter.GetAccessPermission
import spp.cli.util.JsonCleaner
import kotlin.system.exitProcess

class GetAccessPermissions : CliktCommand() {

    override fun run() = runBlocking {
        val response = try {
            PlatformCLI.apolloClient.query(GetAccessPermissionsQuery()).execute()
        } catch (e: Exception) {
            echoError(e)
            if (Main.standalone) exitProcess(-1) else return@runBlocking
        }
        if (response.hasErrors()) {
            echo(response.errors?.get(0)?.message, err = true)
            if (Main.standalone) exitProcess(-1) else return@runBlocking
        }

        echo(JsonCleaner.cleanJson(MapJsonWriter().let {
            it.beginArray()
            response.data!!.getAccessPermissions.forEach { ob ->
                it.beginObject()
                GetAccessPermission.toJson(it, CustomScalarAdapters.Empty, ob)
                it.endObject()
            }
            it.endArray()
            (it.root() as ArrayList<*>)
        }).encodePrettily())
        if (Main.standalone) exitProcess(0)
    }
}
