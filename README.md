# ![](https://github.com/sourceplusplus/live-platform/blob/master/.github/media/sourcepp_logo.svg)

[![License](https://camo.githubusercontent.com/93398bf31ebbfa60f726c4f6a0910291b8156be0708f3160bad60d0d0e1a4c3f/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f736f75726365706c7573706c75732f6c6976652d706c6174666f726d)](LICENSE)
![GitHub release](https://img.shields.io/github/v/release/sourceplusplus/interface-cli?include_prereleases)
[![Build](https://github.com/sourceplusplus/interface-cli/actions/workflows/build.yml/badge.svg)](https://github.com/sourceplusplus/interface-cli/actions/workflows/build.yml)

# What is this?

This project provides a command-line interface to [Source++](https://github.com/sourceplusplus/live-platform), the open-source live coding platform.

# Install

## Quick install

### Linux or macOS

Install the latest version with the following command:

```shell
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/sourceplusplus/interface-cli/master/scripts/install.sh)"
```

### Windows

Note: you need to start cmd or PowerShell in administrator mode.

```shell
curl -LO "https://raw.githubusercontent.com/sourceplusplus/interface-cli/master/scripts/install.bat" && .\install.bat
```

## Install by available binaries

Go to the [releases page](https://github.com/sourceplusplus/interface-cli/releases) to download all available binaries,
including macOS, Linux, Windows.

# Usage

Try executing `spp-cli --help` to output the usage instructions like so:

```
Usage: spp-cli [OPTIONS] COMMAND [ARGS]...

Options:
  -v, --verbose            Enable verbose mode
  -p, --platform TEXT      Source++ platform host
  -c, --certificate PATH   Source++ platform certificate
  -k, --key PATH           Source++ platform key
  -a, --access-token TEXT  Developer access token
  -h, --help               Show this message and exit

Commands:
  admin
  add-breakpoint
  add-log
  add-meter
  add-span
  get-breakpoints
  get-instruments
  get-logs
  get-meters
  get-spans
  remove-instrument
  remove-instruments
  clear-instruments
  subscribe-events    Listens for and outputs live events. Subscribes to all events by default
  get-self
  version
```

To get information about a sub-command, try `spp-cli <command> --help`:

```
Usage: spp-cli admin [OPTIONS] COMMAND [ARGS]...

Options:
  -h, --help  Show this message and exit

Commands:
  add-role
  get-developer-roles
  get-roles
  remove-role
  add-developer-role
  remove-developer-role
  add-role-permission
  get-developer-permissions
  get-role-permissions
  remove-role-permission
  add-developer
  get-developers
  remove-developer
  refresh-developer-token
  add-access-permission
  add-role-access-permission
  get-access-permissions
  get-developer-access-permissions
  get-role-access-permissions
  remove-access-permission
  remove-role-access-permission
  reset
```

# Documentation
- [Developer Commands](https://docs.sourceplusplus.com/implementation/tools/clients/cli/developer/) / [Admin Commands](https://docs.sourceplusplus.com/implementation/tools/clients/cli/admin/)
