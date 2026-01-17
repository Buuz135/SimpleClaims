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