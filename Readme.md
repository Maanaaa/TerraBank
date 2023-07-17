# TerraBank Plugin

**Version:** 1.0

**Author:** Mana  
**Website:** [terracraft.fr](https://terracraft.fr)

TerraBank is a plugin that allows players to interact with a bank system in Minecraft. The plugin introduces banknotes as the primary currency, offering a flexible and convenient way to handle in-game transactions. With TerraBank, you can easily manage player balances, issue banknotes, and provide an immersive banking experience for your players.

## Features

- Bank system with customizable settings
- Banknotes as the main currency
- Integration with the lightEconomy database
- Support for Vault plugin for economy integration

## Installation

To install TerraBank, follow these steps:

1. Download the latest version of TerraBank.
2. Place the TerraBank.jar file into the `plugins` folder of your Minecraft server.
3. Make sure you have the lightEconomy plugin and its database properly set up and configured.
4. Restart your Minecraft server to enable the TerraBank plugin.

## Configuration

TerraBank can be fully customized to fit your server's needs. You can configure various aspects of the bank system, such as the banknote currency symbol, item type, and display name. To connect TerraBank to the lightEconomy database, make sure to provide the correct database information in the configuration file.

![image](https://github.com/Maanaaa/TerraBank/assets/123769327/7f94299f-ce92-4f64-a2b3-b190cf9cf948)

![image](https://github.com/Maanaaa/TerraBank/assets/123769327/2e85ad90-048c-4174-bee7-b7e615d3b8ff)


## Commands

TerraBank provides the following commands:

- `/terrabank` or `/tb` or `/bank` - The main command for TerraBank.
    - Permission: `terrabank.use`

- `/terrabank give <player> <amount>` - Gives a banknote to the specified player.
    - Permission: `terrabank.use`

      ![image](https://github.com/Maanaaa/TerraBank/assets/123769327/3bc51718-b476-49ec-b8c6-dd637be23cb7)

- `/terrabank reload` - Reloads the configuration of TerraBank.
    - Permission: `terrabank.use`
- `/terrabank help` - Displays the help page for TerraBank commands.
    - Permission: `terrabank.use`

      ![image](https://github.com/Maanaaa/TerraBank/assets/123769327/966985e6-6244-49dd-b861-47a3b7303e9d)

## Event
TerraBank include a right-click event to collect the banknote and add money to lightEconomy database

![image](https://github.com/Maanaaa/TerraBank/assets/123769327/46ec6974-aa8f-4205-b645-70b081c0139d)

![image](https://github.com/Maanaaa/TerraBank/assets/123769327/36913c50-03fa-4ac1-92cd-f4a9132c2f0b)



For more details on each command and their usage, please refer to the plugin documentation.

## Support and Feedback

If you encounter any issues or have any questions regarding TerraBank, please contact theo_maanaa on Discord for support. We appreciate your feedback and suggestions for improving the plugin.

Enjoy using TerraBank and create an immersive banking experience for your Minecraft server!
