package spp.cli.commands.admin.developer

import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import developer.AddDeveloperMutation
import kotlinx.coroutines.runBlocking
import spp.cli.Main
import spp.cli.PlatformCLI
import spp.cli.PlatformCLI.apolloClient
import kotlin.system.exitProcess

class AddDeveloper : CliktCommand(printHelpOnEmptyArgs = true) {

    val id by argument(help = "Developer ID")

    override fun run() = runBlocking {
        val response = try {
            apolloClient.mutate(AddDeveloperMutation(id)).await()
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

        echo(response.data!!.addDeveloper().accessToken()!!)
        if (Main.standalone) exitProcess(0)
    }
}
