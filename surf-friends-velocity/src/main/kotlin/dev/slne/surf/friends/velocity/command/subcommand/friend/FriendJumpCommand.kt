package dev.slne.surf.friends.velocity.command.subcommand.friend

//fun CommandAPICommand.friendJumpCommand() = subcommand("jump") {
//    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_JUMP)
//    playerStringArgument("target")
//    playerExecutor { player, args ->
//        container.launch {
//            val target: String by args
//            val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
//                player.uniqueId.sendText {
//                    error("Der Spieler $target wurde nicht gefunden.")
//                }
//            }
//
//            val friendShip = friendService.getFriendship(player.uniqueId, targetUuid)
//
//            if (friendShip == null) {
//                player.uniqueId.sendText {
//                    error("Du bist nicht mit $target befreundet.")
//                }
//                return@launch
//            }
//
//            if (targetUuid.isOnline()) {
//                val onlineFriend =
//                    plugin.proxy.getPlayer(targetUuid).getOrNull() ?: return@launch run {
//                        player.uniqueId.sendText {
//                            error("Der Spieler $target ist nicht online.")
//                        }
//                    }
//
//                player.createConnectionRequest(
//                    onlineFriend.currentServer.getOrNull()?.server ?: return@launch run {
//                        player.uniqueId.sendText {
//                            error("Der Spieler $target ist nicht auf einem Server.")
//                        }
//                    }).fireAndForget()
//
//                player.uniqueId.sendText {
//                    success("Du bist ")
//                    variableValue(target)
//                    success(" auf den Server gefolgt.")
//                }
//                return@launch
//            }
//
//            player.uniqueId.sendText {
//                error("Der Spieler $target ist nicht online.")
//            }
//        }
//    }
//}