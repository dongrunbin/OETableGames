package com.oegame.tablegames.service.game;

import com.oegame.tablegames.model.local.gen.MahjongSettingsDBModel;
import com.oegame.tablegames.model.local.gen.MahjongSettingsEntity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;


public class RoomSettingBase implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String settingStr = "";

	public int cost = 0;
	public int loop = 8;
	public int player = 4;
	public int baseScore = 1;
	public int payment = 0;

	public RoomSettingBase()
	{

	}
	
	@Override
	public String toString()
	{
		return settingStr;
	}

	public void init(List<Integer> rule)
	{
		settingStr = "";
		for (int i = 0; i < rule.size(); ++i)
		{
			settingStr += rule.get(i).toString();
			if (i < rule.size() - 1)
			{
				settingStr += ",";
			}
		}

		List<MahjongSettingsEntity> set = MahjongSettingsDBModel.singleton().getList();

		System.out.println("初始化...Begin.");

		for (MahjongSettingsEntity entity : set)
		{
			try
			{
				Field field = this.getClass().getField(entity.tags);
				if (field == null)
				{
					continue;
				}
				if (entity.status == 0 && entity.selected)
				{
					if (field.getType() == Boolean.TYPE)
					{
						field.set(this, true);
					}
					else if (field.getType() == Integer.TYPE)
					{
						field.set(this, entity.value);
					}
					if (entity.cost > 0)
					{
						this.cost = entity.cost;
					}
				}
				else
				{
					if (entity.status == 1 && entity.selected)
					{
						if (field.getType() == Boolean.TYPE)
						{
							field.set(this, false);
						}
						else if (field.getType() == Integer.TYPE)
						{
							field.set(this, entity.value);
						}
						if (entity.cost > 0)
						{
							this.cost = entity.cost;
						}
					}
				}
			}
			catch (Exception e)
			{

			}
		}

		System.out.println("初始化...End.");
		System.out.println("------------------------------------------------");
		System.out.println("开始设置...Begin.");
		for (int i = 0; i < rule.size(); i++)
		{
			MahjongSettingsEntity entity = MahjongSettingsDBModel.singleton().get(rule.get(i));
			if (entity != null)
			{
				
				System.out.println("entity.tags: " + entity.tags + " selected: " + entity.value + " id: " + rule.get(i)
						+ " - " + entity.name);
				try
				{
					Field field = this.getClass().getField(entity.tags);
					if (field == null)
					{
						System.out.println(entity.tags + "是空的");
						continue;
					}

					if (field.getType() == Boolean.TYPE)
					{
						field.set(this, true);
					}
					else if (field.getType() == Integer.TYPE)
					{
						field.set(this, entity.value);
					}

					if (entity.tags.equals("loop"))
					{
						this.cost = entity.cost;
					}

				}
				catch (Exception e)
				{
					
				}
			}
			else
			{
				System.err.println("rule.get(i): " + rule.get(i));
			}

		}

		System.out.println("开始设置...End.");
		System.out.println("------------------------------------------------");
	}
}
