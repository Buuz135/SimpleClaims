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
