package com.luxof.lessertp.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpTeleport;

import static com.luxof.lessertp.LesserTeleport.assertEntityMayBeTeleported;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class SimplerTPAction implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Entity teleportee = OperatorUtils.getEntity(args, 0, getArgc());
        ctx.assertEntityInRange(teleportee);
        assertEntityMayBeTeleported(teleportee);

        Vec3d offset = OperatorUtils.getVec3(args, 1, getArgc());

        Vec3d posAfterTeleport = teleportee.getPos().add(offset);
        ctx.assertVecInRange(posAfterTeleport);

        if (teleportee.getPos().squaredDistanceTo(posAfterTeleport) > 16*16)
            throw new MishapBadLocation(posAfterTeleport, "too_far");

        if (!HexConfig.server().canTeleportInThisDimension(ctx.getWorld().getRegistryKey()))
            throw new MishapBadLocation(posAfterTeleport, "bad_dimension");

		return new SpellAction.Result(
            new Spell(teleportee, offset),
            (long)(MediaConstants.DUST_UNIT * 2.5),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 3, 10)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        private final Entity teleportee;
        private final Vec3d offset;

        public Spell(Entity teleportee, Vec3d offset) {
            this.teleportee = teleportee;
            this.offset = offset;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
			OpTeleport.INSTANCE.teleportRespectSticky(this.teleportee, this.offset, ctx.getWorld());
		}

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> args, CastingEnvironment env, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
