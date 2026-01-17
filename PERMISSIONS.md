### Simple Claims Commands and Permissions

Here is a comprehensive list of all commands available in Simple Claims, along with their required permissions and
descriptions.

| Command                                                      | Permission                                  | Description                                                                          |
|:-------------------------------------------------------------|:--------------------------------------------|:-------------------------------------------------------------------------------------|
| `/simpleclaims`                                              | `simpleclaims.claim-gui`                    | Opens the chunk claim GUI.                                                           |
| `/simpleclaims claim`                                        | `simpleclaims.claim`                        | Claims the chunk where you are currently located.                                    |
| `/simpleclaims unclaim`                                      | `simpleclaims.unclaim`                      | Unclaims the chunk where you are currently located.                                  |
| `/simpleclaims admin-chunk`                                  | `simpleclaims.admin.admin-chunk`            | Opens the chunk claim GUI in OP mode to claim chunks using the selected admin party. |
| `/simpleclaims admin-claim`                                  | `simpleclaims.admin.admin-claim`            | Claims the chunk for the selected admin party.                                       |
| `/simpleclaims admin-unclaim`                                | `simpleclaims.admin.admin-unclaim`          | Unclaims the chunk (admin override).                                                 |
| `/simpleclaimsparty`                                         | `simpleclaims.edit-party`                   | Opens the party information and editing GUI.                                         |
| `/simpleclaimsparty create`                                  | `simpleclaims.create-party`                 | Creates a new party.                                                                 |
| `/simpleclaimsparty invite <player>`                         | `simpleclaims.create-invite`                | Invites a player to your party.                                                      |
| `/simpleclaimsparty invite-accept`                           | `simpleclaims.accept-invite`                | Accepts your most recent party invite.                                               |
| `/simpleclaimsparty leave`                                   | `simpleclaims.party-leave`                  | Leaves your current party.                                                           |
| `/simpleclaimsparty admin-create <party-name>`               | `simpleclaims.admin.admin-create-party`     | Creates a new party with a specific name (admin tool).                               |
| `/simpleclaimsparty admin-party-list`                        | `simpleclaims.admin.admin-party-list`       | Shows all parties and allows an admin to edit them.                                  |
| `/simpleclaimsparty admin-modify-chunk <amount>`             | `simpleclaims.admin.admin-modify-chunk`     | Changes the chunk limit of the currently selected party.                             |
| `/simpleclaimsparty admin-modify-chunk-all <amount>`         | `simpleclaims.admin.admin-modify-chunk-all` | Changes the chunk limit for all existing parties.                                    |
| `/simpleclaimsparty admin-override`                          | `simpleclaims.admin.admin-override`         | Toggles ignoring all chunk restrictions for the admin.                               |
| `/simpleclaimsparty add-chunk-amount <player-name> <amount>` | `simpleclaims.admin.add-chunk-amount`       | Adds a specific amount of chunks to the party of the specified player.               |

#### Aliases

- `/simpleclaims`: `/sc`, `/sc-chunks`, `/scc`
- `/simpleclaimsparty`: `/scp`, `/sc-party`

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
