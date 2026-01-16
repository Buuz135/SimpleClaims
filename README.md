![alt text](https://media.forgecdn.net/attachments/1432/146/simple_claims-png.png) ![code cat](https://media.forgecdn.net/attachments/1426/453/code_cat-png.png)

[![](https://img.shields.io/twitter/follow/Buuz135mods?color=E04E14&labelColor=2D2D2D&style=for-the-badge)](https://twitter.com/Buuz135mods) [![](https://img.shields.io/badge/DONATE-KOFI-E04E14?labelColor=2D2D2D&style=for-the-badge)](https://ko-fi.com/buuz135) [![](https://img.shields.io/discord/357597633566605313?color=E04E14&labelColor=2D2D2D&label=JOIN-DISCORD&style=for-the-badge)](https://discord.gg/4tPfwjn)

Simple Claims is a claiming mod that allows you to protect chunks from players that aren't in your party.

I'm open to feedback and suggestions on my discord

## Main Features

* Chunk protection from Block Interaction, Block Breaking and Block Placing that can be enabled or disabled by party
* Fully customizable parties using a custom
  GUI ![Party GUI](https://media.forgecdn.net/attachments/1432/171/hytaleclient_hs1fs6ngh3-png.png)
* Custom Hytale Map that shows the claims (can be disabled in the
  config) ![Claim Map](https://media.forgecdn.net/attachments/1432/179/hytaleclient_whayton77o-jpg.jpg)
* Custom Chunk claiming interface for quick claiming/unclaiming
* Admin Party Editing & Usage

[![APEX HOSTING](https://media.forgecdn.net/attachments/1464/785/837x80_hytale-v3-png.png)](https://apexhost.gg/Buuz135)

## <span style="color:#e03e2d">Warning: Currently there is no way to cancel interactions from items like the hammer. Waiting on support from the Hytale team.</span>

## Commands

There is 2 main commands in the mod simpleclaims (aliases: sc, sc-chunks, scc) and simpleclaimsparty (aliases: scp,
sc-party)

### User Commands

* **/sc** - Opens up the quick claiming party GUI
* **/sc claim** - Claims the chunk the user is standing on (will create a party if the user doesn't have one)
* **/sc unclaim** - Unclaims the chunk the user is standing on if the chunk belongs to its party
* **/scp create** - Creates a party and opens the party edit GUI
* **/scp** - Opens the party edit GUI
* **/scp invite <player\_name>** - Invites `player_name` to your party
* **/scp invite-accept** - Accepts your most recent party invite
* **/scp leave** - Leaves your current party, if you are the owner of the party ownership will be transfered to the
  first member for the party, if there aren't any member the party will be removed and all the chunks unclaimed

### Admin Commands

* **/scp admin-party-list** - Opens a GUI showing all the parties that exist and allows you to edit them or use them for
  admin commands
* **/sc admin-claim** - Claims the chunk the user is standing on using the party you have selected using the
  `admin-party-list` command
* **/sc admin-unclaim** - Unclaims the chunk the user is standing on
* **/scp admin-create** - Creates a party without and owner and opens the party edit GUI
* **/scp admin-modify-chunk <amount>** - Changes the chunk amount limit of a party, must have selected a party first
  using the `admin-party-list` command
* **/scp admin-override** - Toggles ignoring all chunk protections for all parties (for you)
* **/sc admin-chunk** - opens the chunk gui to claim chunks using the selected admin party