package com.luxof.lessertp.actions;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapEntityTooFarAway;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpTeleport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class LesserTPAction implements SpellAction {
    public int getArgc() {
        return 2;
    }

    // why
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Entity teleportee = OperatorUtils.getEntity(args, 0, getArgc());
        Either<Double, Vec3d> arg2 = OperatorUtils.getNumOrVec(args, 1, getArgc());
        Vec3d fract;
        if (arg2.left().isPresent()) {
            Double num = clamp(arg2.left().get(), 0.0001, 99.99999999);
            fract = new Vec3d(num, num, num);
        } else {
            Vec3d vec = arg2.right().get();
            fract = new Vec3d(
                clamp(vec.x, 0.0001, 99.99999999),
                clamp(vec.y, 0.0001, 99.99999999),
                clamp(vec.z, 0.0001, 99.99999999)
            );
        }
        
        int cost = (int)(MediaConstants.DUST_UNIT * 0.01);

        try {
            ctx.assertEntityInRange(teleportee);
        } catch (MishapEntityTooFarAway e) {
            // smallminded and java-pilled
            // (presented as a "bug in the mod" mishap tho)
            throw new RuntimeException(e);
        }

        Optional<LivingEntity> caster = Optional.of(ctx.getCastingEntity());
        List<ParticleSpray> particles;
        if (caster.isPresent()) {
            // my logic for these magic numbers:
            // explosion's particle sizes depend on level and there are always 50 particles from explosion
            // and i don't want this spell to lag a lot if you use it a lot
            // so 1/5th the number of particles from a level 3 explosion but same size seems fairly good
            particles = List.of(ParticleSpray.burst(caster.get().getPos(), 3, 10));
        } else {
            particles = List.of();
        }
		return new SpellAction.Result(
            new Spell(teleportee, fract),
            cost,
            particles,
            1
        );
    }

    public class Spell implements RenderedSpell {
        private final Entity teleportee;
        private final Vec3d fract;

        public Spell(Entity teleportee, Vec3d fract) {
            this.teleportee = teleportee;
            this.fract = fract;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            Vec3d entityPos = teleportee.getPos();
            Vec3d goToBase = new Vec3d(
                Math.floor(entityPos.x) - entityPos.x,
                Math.floor(entityPos.y) - entityPos.y,
                Math.floor(entityPos.z) - entityPos.z
            );
            OpTeleport.INSTANCE.teleportRespectSticky(
                teleportee,
                goToBase,
                ctx.getWorld()
            );
            OpTeleport.INSTANCE.teleportRespectSticky(
                teleportee, 
                this.fract, 
                ctx.getWorld()
            );
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
