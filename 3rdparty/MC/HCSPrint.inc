# Include file for printing.
#
# powers -- macro for displaying powers.
#
$$macro powers2(needtab)
{{
@assign _sublistLevel=-1
@foreach(power)

@assign _nf=split(@item,"	", "_cost", "_name", "_end")
@if @indent <= _sublistLevel
@assign _i=@indent
@while _sublistLevel <= _i
ENDSUBLIST
@assign _i=_i - 1

@endwhile
@assign _sublistLevel=_i
@endif

BEGINPOWER:
@if @sublist = 1
Sublist: %@name%
PTS: %_cost%
@assign _sublistLevel=@indent
@else
Power: %@origName%
Name: %@name%
%_name%
PTS: %_cost%
#@if @options
@options()
%@option%
@endoptions
@endif  # End Sublist
ENDPOWER:
@endfor
}}
#
# skills -- macro for printing skills.
#
$$macro skills(needtab)
{{
@foreach(skill)

@assign _nf=split(@item,"	", "_cost", "_name", "_roll")
BEGINPOWER:
@if @sublist = 1
Sublist: %@name%
PTS: %_cost%
@else
Power: %@origName%
Name: %@name%
%_name%
PTS: %_cost%
@if @class = "Agility"
ROLL: %_roll%\
@endif
@if @class = "DEX"
ROLL: %_roll%\
@endif
@if @class = "INT"
ROLL: %_roll%\
@endif
@if @class = "Intellect"
ROLL: %_roll%\
@endif
@if @class = "PRE"
ROLL: %_roll%\
@endif
@if @class = "Interaction"
ROLL: %_roll%\
@endif
@if @class = "General"
ROLL: %_roll%\
@endif

#@if @options
@options()
%@option%
@endoptions
@endif  # End Sublist
ENDPOWER:
@endfor
}}
#
# disads -- macro to print disadvantages.
#
$$macro disads(needtab)
{{
@foreach(disadv)
$$ifdef(needtab)	$$endif%@item%\
@if @options and ConfigParam("PrintOptions")
@options()
@if ConfigParam("SkipZeroOptions")
?(@optType = "Multiplier" and @optvalue = 1) or @optvalue=0??; %@option%?\
@else
; %@option%\
@endif
@endoptions
@endif

@if @notes and ConfigParam("PrintNotes")
$$ifdef(needtab)	$$endif	%@notes%
@endif
@endfor
}}
#
# equipment -- print all equipment
#
$$macro equipment(needtab)
{{
@foreach(Equipment)
$$ifdef(needtab)	$$endif%@format%\
@if @options and ConfigParam("PrintOptions")
@options()
@if ConfigParam("SkipZeroOptions")
?(@optType = "Multiplier" and @optvalue = 1) or @optvalue=0??; %@option%?\
@else
; %@option%\
@endif
@endoptions
@endif

@if @notes and ConfigParam("PrintNotes")
$$ifdef(needtab)	$$endif	%@notes%
@endif
@endfor
}}
#
# info -- output information about character.
#
$$macro info(arg)
{{
@if @`info.story`
Background: %@info.story%

@endif
@assign __needComma=0
@if height != 0
@assign __needComma=1
Height: %height%?m_height!=1?/%round(m_height*height)%??cm (%cmToEnglish(height)%?m_height!=1?/%cmToEnglish(m_height*height)%??)\
@endif
@if weight != 0
?__needComma?, ??\
@assign __needComma=1
Weight: %weight%?m_mass!=1?/%m_mass*weight%??kg (%kgToEnglish(weight)%?m_mass!=1?/%kgToEnglish(m_mass*weight)%??)\
@endif
@if @`info.sex`!=2
?__needComma?, ??\
@assign __needComma=1
Sex: %@info.sex%\
@endif
@if age
?__needComma?, ??\
@assign __needComma=1
Age: %age%\
@endif
@if @`info.race`
?__needComma?, ??\
@assign __needComma=1
Race: %@info.race%
@endif
@if @`info.appearance`

Appearance: %@info.appearance%
@endif
@if @notes

@firstindent 0
@leftindent 0
@keepnext
@bold
Notes
@firstindent .2"
@plain
%@notes%
@endif
}}
#
# nonweapons -- macro for outputting nonweapon equipment
#
$$macro nonweapons(needtab)
{{
@foreach(Equipment)
@if @class <> "Weapon"
$$ifdef(needtab)	$$endif%@format%\
@if @options and ConfigParam("PrintOptions")
@options()
@if ConfigParam("SkipZeroOptions")
?(@optType = "Multiplier" and @optvalue = 1) or @optvalue=0??; %@option%?\
@else
; %@option%\
@endif
@endoptions
@endif

@if @notes and ConfigParam("PrintNotes")
$$ifdef(needtab)	$$endif	%@notes%
@endif
@endif
@endfor
}}
#
# weapTable -- macro for printing weapon table in Hero character sheets.
#
$$macro weapTable(needtab)
{{
@foreach(Equipment)
@if (@class = "Weapon")
%@name%	%@OCV%/?@`R Mod`?%@R Mod%?--?	\
%@Damage%/%@StunX%	%@STR Min%	%@Notes%
@endif
@endfor
}}
#
# baseAreas -- macro for printing base areas.
#
$$macro baseAreas(needtab)
{{
@foreach(Base Areas)
$$ifdef(needtab)	$$endif%@format%\
@if @options>0 and ConfigParam("PrintOptions")
@options()
@if ConfigParam("SkipZeroOptions")
?(@optType = "Multiplier" and @optvalue = 1) or @optvalue=0??; %@option%?\
@else
; %@option%\
@endif
@endoptions
@endif
@if @notes and ConfigParam("PrintNotes")
; %@notes%\
@endif

@endfor
}}
#
# vehicleMovement -- macro for printing vehicle movements.
#
$$macro vehicleMovement(needtab)
{{
@foreach(Movement)
$$ifdef(needtab)	$$endif\
@assign _n = split(@item, "	", "_cost", "_name", "_end")
%_cost%	%_name%\
@if @options and ConfigParam("PrintOptions")
@options()
@if ConfigParam("SkipZeroOptions")
?(@optType = "Multiplier" and @optvalue = 1) or @optvalue=0??; %@option%?\
@else
; %@option%\
@endif
@endoptions
@if @notes and ConfigParam("PrintNotes")
; %@notes%
@endif
@if _end
	%_end%\
@endif
@endif

@endfor
}}
#
# Combat maneuvers
#
$$macro combatMan(arg)
{{
Block	½	--	+0	stops attacks, abort
Brace	0	+2	½	+2 vs. RMod
Disarm	½	-2	0	disarm: STR vs. STR
Dodge	½	--	+3	vs. all attacks, abort
Grab	½	-1	-2	grab, do STR
Haymaker	½	0	 -5	%round(total_STR*3/5)/2%d6 bef. Pushing
Move By	½	-2	-2	%round(total_STR/5)/2%d6+v/5
Move Through	½	-v/5	-3	%round((total_STR*2-2)/5)/2%d6+v/3
Set	1	+1	0
Strike	½	+0	+0	STR or weapon type
Other Attacks	½	+0	+0
Covered	½	-2*	+0	target held at gunpoint
Dive for Cover	½	+0	+0	moves character, abort
Pull a Punch	½	-1/3d6	0	full STUN, ½ BODY
Roll w/Punch	½	-2	-2	½ STUN/BODY, abort
Snap Shot	1	-1	0	duck behind cover
Suppression	1	-2	+0	continuous autofire
Sweep	1	-2	×½	hit mult. targets HTH
@foreach(Skill)
@if strindex(@category, "Martial Maneuver")
%@name%	½	%@ocv%	%@dcv%	%@damage%?@notes?. %@notes%??
@endif
@endfor
}}
#
# Display combat levels.
#
$$macro dispLevels(arg)
{{
Levels: \
@assign x_foundskill=0
@foreach(Skill)
@if @class = "Combat Level"
@if x_foundskill
, \
@endif
@assign x_foundskill=1
+%@level% %@name%\
@endif
@endfor
@foreach(Power)
@if @class = "Combat Level"
@if x_foundskill
, \
@endif
@assign x_foundskill=1
+%@level% %@name%\
@endif
@endfor
}}
#
# Display combat stats.
#
$$macro combStat(arg)
{{
DEX: %DEX%%t_dex%, SPD: %integer(spd)%%t_spd%, ECV: %ecv%
Phases: %phases%
PD/rPD: %d_pd%/%total_rPD%    ED/rED: %d_ed%/%total_rED%    MD: %t_mentalDef%
END: ______   STUN: ______   BODY: ______
Lift: %t_lift%
}}
$$macro printSectionDefenses(printNames)
{{\
@array _sdED[0]
@array _sdPD[0]
@array _cvgArray[0]
@assign _i = 1
@assign _hasArmor=0
@while _i<=18
	@assign _n=setElement(_sdED, _i, 0)
	@assign _n=setElement(_sdPD, _i, 0)
	@assign _i=_i+1
@endwhile
@foreach(Equipment)
	@if integer(optValue('Armor Worn'))
		@assign _hasArmor=1
		@if (inCategory('Sectional Defense'))
			@assign _areas = replaceString(optValue('Coverage'), ' ', '');
		@else
			@assign _cv = integer(10*itemInfo('`Coverage`'));
			@assign _areas = qindex(_cv-1, '12-13', '11-13', 0, '5,10-13', '4-5,9-13', '3-5,9-14,16-18', '3-5,7-14,16-18', '3-14,16-18', '3-18');
		@endif
		@if optPresent('DEF')
			@assign _ed = integer(optValue('DEF'));
			@assign _pd = _ed;
		@else
			@assign _ed = integer(optValue('ED'));
			@assign _pd = integer(optValue('PD'));
		@endif
		@assign _n = split(_areas, ',', _cvgArray);
		@assign _i = 1;
		@while (_i <= sizeof(_cvgArray))
			@assign _n = split(_cvgArray[_i], '-', '_loc1', '_loc2');
			@if (!_loc2)
				@assign _first = integer(_loc1)
				@assign _last = integer(_loc1)
			@else
				@assign _first = integer(_loc1)
				@assign _last = integer(_loc2);
			@endif
			@while (_first <= _last)
				@assign _n = setElement(_sdED, _first, max(_ed,_sdED[_first]));
				@assign _n = setElement(_sdPD, _first, max(_pd,_sdPD[_first]));
				@assign _first = _first + 1;
			@endwhile
			@assign _i = _i + 1;
		@endwhile
	@endif
@endforeach
@if _hasArmor
	@array _armorED[0]
	@array _armorPD[0]
	@array _sectName[0]
	@assign _n=removeAllElements(_armorED);
	@assign _n=removeAllElements(_armorPD);
	@assign _n=removeAllElements(_sectName);
# Head
	@assign _n=addElement(_sectName, "Head")
	@assign _n=addElement(_armorED, max(_sdED[3], _sdED[4], _sdED[5]))
	@assign _n=addElement(_armorPD, max(_sdPD[3], _sdPD[4], _sdPD[5]))
# Hands
	@assign _n=addElement(_sectName, "Hands")
	@assign _n=addElement(_armorED, _sdED[6])
	@assign _n=addElement(_armorPD, _sdPD[6])
# Arms
	@assign _n=addElement(_sectName, "Arms")
	@assign _n=addElement(_armorPD, max(_sdPD[7], _sdPD[8]))
	@assign _n=addElement(_armorED, max(_sdED[7], _sdED[8]))
# Shoulders
	@assign _n=addElement(_sectName, "Shoulders")
	@assign _n=addElement(_armorED, _sdED[9])
	@assign _n=addElement(_armorPD, _sdPD[9])
# Chest
	@assign _n=addElement(_sectName, "Chest")
	@assign _n=addElement(_armorPD, max(_sdPD[10], _sdPD[11]))
	@assign _n=addElement(_armorED, max(_sdED[10], _sdED[11]))
# Stomach
	@assign _n=addElement(_sectName, "Stomach")
	@assign _n=addElement(_armorED, _sdED[12])
	@assign _n=addElement(_armorPD, _sdPD[12])
# Vitals
	@assign _n=addElement(_sectName, "Vitals")
	@assign _n=addElement(_armorED, _sdED[13])
	@assign _n=addElement(_armorPD, _sdPD[13])
# Thighs
	@assign _n=addElement(_sectName, "Thighs")
	@assign _n=addElement(_armorED, _sdED[14])
	@assign _n=addElement(_armorPD, _sdPD[14])
# Legs
	@assign _n=addElement(_sectName, "Legs")
	@assign _n=addElement(_armorPD, max(_sdPD[15], _sdPD[16]))
	@assign _n=addElement(_armorED, max(_sdED[15], _sdED[16]))
# Feet
	@assign _n=addElement(_sectName, "Feet")
	@assign _n=addElement(_armorPD, max(_sdPD[17], _sdPD[18]))
	@assign _n=addElement(_armorED, max(_sdED[17], _sdED[18]))
# Output table.
	@assign _i=1
	@while _i<=sizeof(_armorPD)
$$ifdef(printNames)%_sectName[_i]%:	$$endif\
		@if (_armorPD[i] or _armorED[_i])
?_armorPD[_i] <> _armorED[_i]?%_armorPD[_i]%/%_armorED[_i]%?%_armorPD[_i]%?
		@else

		@endif
		@assign _i=_i+1
	@endwhile
@endif
}}
