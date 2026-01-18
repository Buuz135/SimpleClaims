# Simple Claims Configuration

This document explains all the configuration options available in the `SimpleClaims` mod.

## Default Party Settings

These settings define the initial permissions and limits for newly created parties.

- **DefaultPartyClaimsAmount** (Integer, Default: `25`): The maximum number of chunks a party can claim by default.
- **MaxPartyMembers** (Integer, Default: `-1`): The maximum number of members a party can have. Use `-1` for no limit.
- **MaxPartyAllies** (Integer, Default: `-1`): The maximum number of allies (both players and other parties) a party can
  have. Use `-1` for no limit.
- **DefaultPartyBlockPlaceEnabled** (Boolean, Default: `false`): Whether block placement is allowed for non-members in
  claimed chunks by default.
- **DefaultPartyBlockBreakEnabled** (Boolean, Default: `false`): Whether block breaking is allowed for non-members in
  claimed chunks by default.
- **DefaultPartyBlockInteractEnabled** (Boolean, Default: `false`): Whether general block interaction is allowed for
  non-members in claimed chunks by default.
- **DefaultPartyPVPEnabled** (Boolean, Default: `false`): Whether PVP is enabled within claimed chunks by default.
- **DefaultPartyFriendlyFireEnabled** (Boolean, Default: `false`): Whether PVP between members of the same party is
  enabled by default.
- **DefaultPartyAllowEntry** (Boolean, Default: `true`): Whether non-members are allowed to enter claimed chunks by
  default.
- **DefaultPartyInteractChest** (Boolean, Default: `false`): Whether non-members can interact with chests in claimed
  chunks by default.
- **DefaultPartyInteractDoor** (Boolean, Default: `false`): Whether non-members can interact with doors in claimed
  chunks by default.
- **DefaultPartyInteractBench** (Boolean, Default: `false`): Whether non-members can interact with benches in claimed
  chunks by default.
- **DefaultPartyInteractChair** (Boolean, Default: `false`): Whether non-members can interact with chairs in claimed
  chunks by default.
- **DefaultPartyInteractPortal** (Boolean, Default: `false`): Whether non-members can interact with portals in claimed
  chunks by default.

## Permission Settings (Allow Changes)

These settings control whether party owners are allowed to change specific permissions in their own party settings.

- **AllowPartyPVPSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle PVP settings for their
  claims.
- **AllowPartyFriendlyFireSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle friendly fire
  settings for their party.
- **AllowPartyPlaceBlockSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle block placement
  permissions.
- **AllowPartyBreakBlockSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle block breaking
  permissions.
- **AllowPartyInteractBlockSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle general block
  interaction permissions.
- **AllowPartyAllowEntrySettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle entry
  permissions.
- **AllowPartyInteractChestSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle chest
  interaction permissions.
- **AllowPartyInteractDoorSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle door
  interaction permissions.
- **AllowPartyInteractBechSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle bench
  interaction permissions.
- **AllowPartyInteractChairSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle chair
  interaction permissions.
- **AllowPartyInteractPortalSettingChanges** (Boolean, Default: `true`): If `true`, party owners can toggle portal
  interaction permissions.

## World & Protection Settings

Global settings that affect claiming and protection across the server.

- **WorldClaimBlacklist** (String Array, Default: `[]`): A list of world names where claiming chunks is disabled.
- **FullWorldProtection** (String Array, Default: `[]`): A list of world names where the entire world is protected as if
  it were claimed.
- **TitleTopClaimTitleText** (String, Default: `"Simple Claims"`): The text displayed at the top of the claim UI.
- **CreativeModeBypassProtection** (Boolean, Default: `false`): If `true`, players in Creative Mode will bypass all
  claim protections.
- **BlocksThatIgnoreInteractRestrictions** (String Array, Default: `["gravestone"]`): A list of block IDs that can
  always be interacted with, even in claimed chunks where interactions are otherwise restricted.

## Visual & Map Settings

Settings related to the user interface and visual feedback.

- **EnableParticleBorders** (Boolean, Default: `true`): If `true`, particles will be used to show the boundaries of
  claimed chunks.
- **ForceSimpleClaimsChunkWorldMap** (Boolean, Default: `true`): If `true`, forces the use of the Simple Claims chunk
  map.

## Experimental Settings

Use these with caution as they might still be in development.

- **EXPERIMENTAL-EnableAlloyEntryTesting** (Boolean, Default: `false`): Enables experimental entry testing logic.
