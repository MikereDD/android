# HA-Verity Upstream Sync

## Remotes

Expected local remote layout:

- `origin` -> private Forgejo canonical repository
- `github` -> GitHub fork
- `upstream` -> `https://github.com/home-assistant/android.git`

## Policy

Keep the upstream-derived default branch as clean as practical.

HA-Verity development occurs on feature branches, beginning with:

`feature/ha-verity-foundation`

## Recommended sync workflow

```powershell
git fetch --all --prune
git switch main
git status
git merge --ff-only upstream/main
git push origin main
git push github main
git switch feature/ha-verity-foundation
git rebase main
```

If upstream changes conflict with HA-Verity, resolve deliberately and update
`docs/ha-verity/OVERLAY.md` when the divergence changes.

## Rule

Never mix an upstream synchronization commit with unrelated HA-Verity feature work.
