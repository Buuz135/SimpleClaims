### Simple Claims Commands and Permissions

Here is a comprehensive list of all commands available in Simple Claims, along with their required permissions and
descriptions.

| Command                                                        | Permission                                               | Description                                                                             |
|:---------------------------------------------------------------|:---------------------------------------------------------|:----------------------------------------------------------------------------------------|
| `/simpleclaims`                                                | `simpleclaims.claim-gui`                                 | Opens the chunk claim GUI.                                                              |
| `/simpleclaims claim`                                          | `simpleclaims.claim`                                     | Claims the chunk where you are currently located.                                       |
| `/simpleclaims unclaim`                                        | `simpleclaims.unclaim`                                   | Unclaims the chunk where you are currently located.                                     |
| `/simpleclaims admin-chunk`                                    | `simpleclaims.admin.admin-chunk`                         | Opens the chunk claim GUI in OP mode to claim chunks using the selected admin party.    |
| `/simpleclaims admin-claim`                                    | `simpleclaims.admin.admin-claim`                         | Claims the chunk for the selected admin party.                                          |
| `/simpleclaims admin-unclaim`                                  | `simpleclaims.admin.admin-unclaim`                       | Unclaims the chunk (admin override).                                                    |
| `/simpleclaimsparty`                                           | `simpleclaims.edit-party`                                | Opens the party information and editing GUI.                                            |
| `/simpleclaimsparty create`                                    | `simpleclaims.create-party`                              | Creates a new party.                                                                    |
| `/simpleclaimsparty invite <player>`                           | `simpleclaims.create-invite`                             | Invites a player to your party.                                                         |
| `/simpleclaimsparty invite-accept`                             | `simpleclaims.accept-invite`                             | Accepts your most recent party invite.                                                  |
| `/simpleclaimsparty leave`                                     | `simpleclaims.party-leave`                               | Leaves your current party.                                                              |
| `/simpleclaimsparty chat-toggle`                               | `simpleclaims.edit-party`                                | Toggles the party chat.                                                                 |
| `/simpleclaimsparty admin-create <party-name>`                 | `simpleclaims.admin.admin-create-party`                  | Creates a new party with a specific name (admin tool).                                  |
| `/simpleclaimsparty admin-party-list`                          | `simpleclaims.admin.admin-party-list`                    | Shows all parties and allows an admin to edit them.                                     |
| `/simpleclaimsparty admin-modify-chunk <amount>`               | `simpleclaims.admin.admin-modify-chunk`                  | Changes the chunk limit of the currently selected party.                                |
| `/simpleclaimsparty admin-modify-chunk-all <amount>`           | `simpleclaims.admin.admin-modify-chunk-all`              | Changes the chunk limit for all existing parties.                                       |
| `/simpleclaimsparty admin-override`                            | `simpleclaims.admin.admin-override`                      | Toggles ignoring all chunk restrictions for the admin.                                  |
| `/simpleclaimsparty add-chunk-amount <player-name> <amount>`   | `simpleclaims.admin.add-chunk-amount`                    | Adds a specific amount of chunks to the party of the specified player.                  |
| `/simpleclaimsparty remove-chunk-amount <player-name> <amount>` | `simpleclaims.admin.remove-chunk-amount`                  | Remove a specific amount of bonus chunks to the party of the specified player.          |
| N/A                                                            | `simpleclaims.party.claim_chunk_amount.<amount>`         | Sets the base amount of chunks a player's party can claim.                              |
| N/A                                                            | `simpleclaims.admin.max_add_chunk_amount.<amount>`       | Sets the maximum amount of chunks a party can reach using the add-chunk-amount command. |
| N/A                                                            | `simpleclaims.player.claim_chunk_gain_minutes.<amount>`  | Sets how often (in minutes) a player will gain another claim chunk by play time.        |

#### Aliases

- `/simpleclaims`: `/sc`, `/sc-chunks`, `/scc`, `/chunk`, `/chunks`, `/claim`, `/claims`
- `/simpleclaimsparty`: `/scp`, `/sc-party`, `/party`
- `/simpleclaimsparty chat-toggle`: `/ct`, `/pc`, `/party-chat`

### Configuring Permissions with Hyales base permissions

You just need to exectue these commands in the Hytale console:

- `/perm group add Adventure simpleclaims.claim`
- `/perm group add Adventure simpleclaims.unclaim`
- `/perm group add Adventure simpleclaims.claim-gui`
- `/perm group add Adventure simpleclaims.edit-party`
- `/perm group add Adventure simpleclaims.create-party`
- `/perm group add Adventure simpleclaims.create-invite`
- `/perm group add Adventure simpleclaims.accept-invite`
- `/perm group add Adventure simpleclaims.party-leave`
- `/perm group add OP simpleclaims.admin.*`
- `/perm user add <playername> simpleclaims.party.claim_chunk_amount.50`

### Configuring Permissions with LuckPerms

To manage permissions for Simple Claims using LuckPerms in Hytale, follow these steps:

1. **Open the LuckPerms Editor:**
   Run the command `/lp editor` in-game. This will provide you with a URL to the web-based editor.

2. **Select a Group or User:**
   In the LuckPerms editor, navigate to the **Groups** or **Users** section on the left sidebar. Choose the group (e.g.,
   `default`, `admin`) or player you want to modify.

3. **Add Permissions:**
   In the "Add Permission" box, type the desired permission from the table above (e.g., `simpleclaims.claim`).
    - Set the value to `true` to grant the permission.
    - Set the value to `false` to explicitly deny it.

4. **Example Configuration:**
    - For regular players, you might want to add:
        - `simpleclaims.claim-gui`
        - `simpleclaims.claim`
        - `simpleclaims.unclaim`
        - `simpleclaims.edit-party`
        - `simpleclaims.create-party`
        - `simpleclaims.create-invite`
        - `simpleclaims.accept-invite`
        - `simpleclaims.party-leave`
    - For admins, you would add `simpleclaims.admin.*` to grant all administrative permissions.

5. **Save Changes:**
   Click the **Save** button in the top right corner of the editor. Copy the command provided and paste it into the
   Hytale console or in-game chat to apply the changes.

Alternatively, you can add permissions directly via commands:

- `/lp group default permission set simpleclaims.claim true`
- `/lp user <playername> permission set simpleclaims.admin.admin-override true`

### Dynamic Permissions

Some permissions are dynamic and require a value to be appended to the end of the permission node.

- `simpleclaims.party.claim_chunk_amount.<amount>`: Sets the maximum amount of chunks a player's party can claim. For
  example, to allow a player to claim 50 chunks, you would grant the permission
  `simpleclaims.party.claim_chunk_amount.50`. If a player has multiple permissions of this type, the highest value will
  be used. In this case only the party owner is checked for this permission. **THE COMMAND TO CHANGE THE AMOUNT OF
  CHUNKS FOR A PARTY WILL TAKE PREFERENCE WHEN CALCULATING THE AMOUNT OF CHUNKS**

- `simpleclaims.admin.max_add_chunk_amount.<amount>`: Sets the maximum amount of chunks a party can reach when using the
  `add-chunk-amount` command. This overrides the `MaxAddChunkAmount` config value if the permission value is higher.

- `simpleclaims.player.claim_chunk_gain_minutes.<amount>`: Sets how often (in minutes) a player will gain another claim
  chunk by play time. This overrides the `ClaimChunkGainInMinutes` config value. Only works if the value is greater than
  0.