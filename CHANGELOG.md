# 1.0.23

* Fixed Bucket & Watering Can interaction not working properly, closes #140

# 1.0.22

* Updated mod to game update 24-01-2026, closes #137

# 1.0.21

* Fixed database not being created properly on first run, closes #130
* Fixed not being able to use the builders workbench closes #129

# 1.0.20

* World map now shows in the claim chunks GUI - ThatGravyBoat
* Fix Creative Mode block breaks not being blocked + Fix WorldEventSystem block damage from breaking claimed chunks. -
  Jaxkr
* Fix coordinate rounding error for negative values due to narrowing casts - XAgent1990
* Added claim amount permission, more info in CONFIG.md, closes #66
* Fixed Admin Claiming Gui not working properly, closes #125
* Removing saving thread to avoid sometimes not saving data in big environments, closes #119 closes #97
* Added a claiming bypass for chunks that dont have valid parties, closes #108
* Fix: fixed issue where players can use benches to craft using resources from others' claimed chests - spencerbass
* Fixed `BlocksThatIgnoreInteractRestrictions` config not working properly, closes #112
* Fixed interaction setting overriding block placing and breaking settings

# 1.0.19

* Replaced interactions with custom ones to allow claim protection, closes #40
* Fixed library conflicts, closes #98
* Removed party ownership transfer when leaving a party, closes #100
* Added a config option to disband parties for inactivity, all the players need to be inactive for the party to be
  disbanded, closes #74
* Added extra aliases to commands
* Replaced bucket interactions with custom ones to allow claim protection, closes #19

# 1.0.18

* Fixed not shipping SQLite dependency, closes #94 closes #95

# 1.0.17

* Converted data storage to SQLite for better stability. BACKUP YOUR DATA! SimpleClaims will automatically migrate your
  data to the new
  format. All the data is stored inside the Universe folder. Closes #91, closes #86

# 1.0.16

* Fixed save button going off screen

# 1.0.15

* Added a claimed chunks list for the party, closes #50
* Added a config option to change the title top line when entering a claim, closes #16
* Fixed a bug where the admin-modify-chunk-all command wasn't updating parties amount if they had the same amount as the
  config
* Added a config to filly protect worlds, closes #73
* [EXPERIMENTAL - ENABLED ON THE CONFIG] Added a allow entry party setting that stops players from entering a claimed
  chunk if they arent allowed. This is workaround for hytale's interaction bugs #40 #19
* Added extra precautions for when file loading fails and added backups of those files
* Added a chunk border system that will show particles when close to an edge of a claimed chunk, closes #79
* Added a config option to configure blocks that will ignore all the restrictions
* Added an option to remove pvp between party members (Friendly Fire), closes #23
* Added a config option to limit how many party member and allies can a party have closes #20
* Improved map so less detail is lost in claimed chunks
* Added a config option to show the claim name on the map, disabled by default, closes #60

# 1.0.14

* Fixed inviting players to a party using the GUI not working properly
* Changed containers to use decorated containers

# 1.0.13

* Filter out players that have a party from the invite dropdown
* Party Chunk Amount Override is now ignored if the party has the same amount of chunks as the config so when the config
  changes it will use the config value
* If the force simple claims map is off, now the default map will be forced
* Added a command to add chunk to a party based on a player name /scp add-chunk-amount <player> <amount>
* Added a dimension blacklist config option, to blacklist which dimension players can claim chunks in
* Added custom permissions for everything, closes #53
* Added config option to disable toggling the party settings, like place block, break block, interact block, AND THE
  OPTION FOR PVP HAS BEEN RENAMED, closes #42 and allow admins to modify those settings even if disabled closes #43

# 1.0.12

* Changed custom map to use the default map settings, closes #37
* Changed block break event system to use DamageBlockEvent instead of BreakBlockEvent, closes #27

# 1.0.11

* Players can now be invited to a party from the edit party screen closes #15
* Pending player invites now show in the member list of the party
* Added ally system to parties, closes #28
* Added a button to the admin party screen to open the claim chunks gui in admin mode, closes #31
* Added a command to change the chunk amount for all parties, closes #14
* Added a config option to disable the PVP toggle, closes #25

# 1.0.10

* Fixed Map Ticking task running off thread when checking for components, closes #18

# 1.0.9

* Fixed players not being removed from the player to party cache when kicked from a party

# 1.0.7 & 1.0.8

* Fixed F key pickup not being protected in claimed chunks
* Fixed world map not updating after claiming/unclaiming chunks
* Fixed admin override not persisting across server restarts
* Added Creative mode bypass option for admin override
* Fixed thread safety issues with concurrent map access
* Fixed ChunkInfo codec parameter naming inconsistency
* Performance: Optimized TitleTickingSystem to reduce allocations
* Performance: ClaimManager now uses O(1) lookups for party/claim operations
* Performance: PartyInfo now uses O(1) lookups for member/override checks
* Changed the category name for the chunk config to make it more clear what it does
* Added a way to remove parties from the admin party list
* Added PVP protection for claims

# 1.0.6

* Reworked how files are loaded and saved to be more reliable, old files should be compatible with the new system

# 1.0.5

* Added /sc admin-chunk to open the chunk gui to claim chunks using the selected admin party