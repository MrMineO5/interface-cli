package spp.cli.commands.admin.role

import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.runBlocking
import role.RemoveDeveloperRoleMutation
import spp.cli.Main
import spp.cli.PlatformCLI
import spp.cli.PlatformCLI.apolloClient
import kotlin.system.exitProcess

class RemoveDeveloperRole : CliktCommand(printHelpOnEmptyArgs = true) {

    val id by argument(help = "Developer ID")
    val role by argument(help = "Role name")

    override fun run() = runBlocking {
        val response = try {
            apolloClient.mutate(RemoveDeveloperRoleMutation(id, role)).await()
        } catch (e: ApolloException) {
            echo(e.message, err = true)
            if (PlatformCLI.verbose) {
                echo(e.stackTraceToString(), err = true)
            }
            if (Main.standalone) exitProcess(-1) else return@runBlocking
        }
        if (response.hasErrors()) {
            echo(response.errors?.get(0)?.message, err = true)
            if (Main.standalone) exitProcess(-1) else return@runBlocking
        }

        if (response.data!!.removeDeveloperRole()) {
            if (Main.standalone) exitProcess(0)
        } else {
            if (Main.standalone) exitProcess(-1) else return@runBlocking
        }
    }
}
