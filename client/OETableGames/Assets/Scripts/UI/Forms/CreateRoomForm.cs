//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 1:52:26
//Description ：
//===================================================
using DrbFramework.DataTable;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CreateRoomForm : FormBase
{
    [SerializeField]
    protected ToggleGroup m_TypeContainer;
    [SerializeField]
    protected ToggleGroup m_GameContainer;
    [SerializeField]
    protected Transform m_SettingContainer;
    [SerializeField]
    protected GameObject m_TypePrefab;
    [SerializeField]
    protected GameObject m_GamePrefab;
    [SerializeField]
    protected GameObject m_GroupPrefab;
    [SerializeField]
    protected GameObject m_OptionPrefab;
    [SerializeField]
    private Button m_btnCreate;

    protected List<UIItemGameType> m_ListType = new List<UIItemGameType>();

    protected List<UIItemToggleGame> m_ListGame = new List<UIItemToggleGame>();

    protected List<UIItemSettingGroup> m_ListGroup = new List<UIItemSettingGroup>();

    protected List<UIItemOption> m_ListOption = new List<UIItemOption>();

    public Action<int> onGameChanged;

    protected UIItemToggleGame m_CurrentGame;

    public override void OnInit()
    {
        base.OnInit();
        InitView();
    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == m_btnCreate.gameObject)
        {
            CreateRoom();
        }
    }

    private void InitView()
    {
        if (m_ListGame.Count > 0) return;
        ICollection<GamesDataEntity> gameTable = DrbComponent.DataTableSystem.GetDataTable<GamesDataEntity>().GetEntities();
        List<string> allType = new List<string>();
        foreach (GamesDataEntity entity in gameTable)
        {
            if (!allType.Contains(entity.GameType))
            {
                allType.Add(entity.GameType);
            }
            GameObject go = Instantiate(m_GamePrefab);
            go.SetActive(true);
            go.SetParentAndReset(m_GameContainer.transform);
            UIItemToggleGame item = go.GetComponent<UIItemToggleGame>();
            item.GameName = entity.GameName;
            item.GameType = entity.GameType;
            item.GameId = entity.Id;
            item.SetToggleGroup(m_GameContainer);
            item.onValueChanged = OnGameChanged;
            m_ListGame.Add(item);
        }
        if (allType.Count > 1)
        {
            for (int i = 0; i < allType.Count; ++i)
            {
                GameObject go = Instantiate(m_TypePrefab);
                go.SetActive(true);
                go.SetParentAndReset(m_TypeContainer.transform);
                UIItemGameType item = go.GetComponent<UIItemGameType>();
                item.GameType = allType[i];
                item.onValueChanged = OnTypeChanged;
                item.SetToggleGroup(m_TypeContainer);
                m_ListType.Add(item);
            }
        }

        InitOption();
    }

    public virtual void SetCurrentType(string type)
    {
        for (int i = 0; i < m_ListType.Count; ++i)
        {
            if (m_ListType[i].GameType.Equals(type))
            {
                m_ListType[i].isOn = true;
            }
            else
            {
                m_ListType[i].isOn = false;
            }
        }
    }

    public virtual void SetCurrentGame(int id)
    {
        for (int i = 0; i < m_ListGame.Count; ++i)
        {
            if (m_ListGame[i].GameId.Equals(id))
            {
                m_ListGame[i].isOn = true;
                break;
            }
        }
    }

    protected virtual void OnTypeChanged(string type, bool isOn)
    {
        if (!isOn) return;
        for (int i = 0; i < m_ListGame.Count; ++i)
        {
            m_ListGame[i].gameObject.SetActive(m_ListGame[i].GameType.Equals(type));
        }
        bool isExists = false;
        for (int i = 0; i < m_ListGame.Count; ++i)
        {
            if (m_ListGame[i].GameType.Equals(type))
            {
                if (m_ListGame[i].isOn == true)
                {
                    OnGameChanged(m_ListGame[i].GameId, true);
                    isExists = true;
                    break;
                }
            }
        }
        if (!isExists)
        {
            for (int i = 0; i < m_ListGame.Count; ++i)
            {
                if (m_ListGame[i].GameType.Equals(type))
                {
                    m_ListGame[i].isOn = true;
                    OnGameChanged(m_ListGame[i].GameId, true);
                    break;
                }
            }
        }
    }

    protected virtual void OnGameChanged(int gameId, bool isOn)
    {
        if (!isOn) return;
        for (int i = 0; i < m_ListGame.Count; ++i)
        {
            if (m_ListGame[i].GameId == gameId)
            {
                m_CurrentGame = m_ListGame[i];
                break;
            }
        }
        if (onGameChanged != null)
        {
            onGameChanged(gameId);
        }
    }

    private void InitOption()
    {
        for (int i = 0; i < m_ListGroup.Count; ++i)
        {
            m_ListGroup[i].gameObject.SetActive(false);
        }
        for (int i = 0; i < m_ListOption.Count; ++i)
        {
            m_ListOption[i].gameObject.SetActive(false);
        }
        ICollection<SettingsDataEntity> options = DrbComponent.DataTableSystem.GetDataTable<SettingsDataEntity>().GetEntities();
        foreach (SettingsDataEntity option in options)
        {
            bool isExists = false;
            for (int j = 0; j < m_ListGroup.Count; ++j)
            {
                if (m_ListGroup[j].GroupName.Equals(option.Label))
                {
                    isExists = true;
                    break;
                }
            }

            if (!isExists)
            {
                GameObject groupGO = Instantiate(m_GroupPrefab);
                groupGO.SetActive(true);
                groupGO.SetParentAndReset(m_SettingContainer);
                UIItemSettingGroup Group = groupGO.GetComponent<UIItemSettingGroup>();
                Group.GroupName = option.Label;
                m_ListGroup.Add(Group);
            }

            if (option.Mode == (int)SelectMode.Loop)
            {
                isExists = false;
                for (int j = 0; j < m_ListOption.Count; ++j)
                {
                    if (m_ListOption[j].OptionTags.Equals(option.Tags))
                    {
                        SelectContent newContent = new SelectContent();
                        newContent.OptionId = option.Id;
                        newContent.OptionName = option.Name;
                        m_ListOption[j].Content.Add(newContent);
                        if (option.Selected)
                        {
                            m_ListOption[j].Index = m_ListOption[j].Content.Count - 1;
                        }
                        isExists = true;
                        break;
                    }
                }
                if (isExists) continue;
            }
            GameObject go = Instantiate(m_OptionPrefab);
            go.SetActive(true);
            UIItemOption item = go.GetComponent<UIItemOption>();
            item.OptionId = option.Id;
            item.OptionName = option.Name;
            item.GroupName = option.Label;
            item.OptionTags = option.Tags;
            item.onValueChanged = OnOptionValueChanged;
            item.Mode = (SelectMode)option.Mode;
            item.isOn = option.Selected;
            item.Cost = option.Cost;
            item.Value = option.Value;
            item.SetGroup(null);
            item.Content.Clear();
            SelectContent content = new SelectContent();
            content.OptionId = option.Id;
            content.OptionName = option.Name;
            item.Content.Add(content);
            if (option.Selected)
            {
                item.Index = item.Content.Count - 1;
            }
            item.isDisplay = option.Status == 1;
            for (int j = 0; j < m_ListGroup.Count; ++j)
            {
                if (m_ListGroup[j].GroupName.Equals(option.Label))
                {
                    item.gameObject.SetParentAndReset(m_ListGroup[j].ToggleGroup.transform);
                    if (item.Mode == SelectMode.Single)
                    {
                        item.SetGroup(m_ListGroup[j].ToggleGroup);
                    }
                    break;
                }
            }
            m_ListOption.Add(item);
        }

        int GroupCount = 0;
        for (int i = 0; i < m_ListGroup.Count; ++i)
        {
            if (m_ListGroup[i].isActiveAndEnabled)
            {
                ++GroupCount;
            }
        }

        VerticalLayoutGroup containerLayout = m_SettingContainer.GetComponent<VerticalLayoutGroup>();
        containerLayout.spacing = (6 - GroupCount) * 30 + (6 - GroupCount) * 5;
        containerLayout.spacing = Mathf.Clamp(containerLayout.spacing, 0, 100);

        if (m_CurrentGame == null) return;
        int playerCount = 1;
        for (int i = 0; i < m_ListOption.Count; ++i)
        {
            if (m_ListOption[i].OptionTags.Equals("player") && m_ListOption[i].isOn)
            {
                playerCount = m_ListOption[i].Value;
                break;
            }
        }
    }

    protected virtual void OnOptionValueChanged(UIItemOption option, bool isOn)
    {
        int GroupCount = 0;
        for (int i = 0; i < m_ListGroup.Count; ++i)
        {
            if (m_ListGroup[i].isActiveAndEnabled)
            {
                ++GroupCount;
            }
        }

        VerticalLayoutGroup containerLayout = m_SettingContainer.GetComponent<VerticalLayoutGroup>();
        containerLayout.spacing = (6 - GroupCount) * 30 + (6 - GroupCount) * 5;
        containerLayout.spacing = Mathf.Clamp(containerLayout.spacing, 0, 100);
    }

    private void CreateRoom()
    {
        List<int> selected = new List<int>();
        for (int i = 0; i < m_ListOption.Count; ++i)
        {
            for (int j = 0; j < m_ListOption[i].Content.Count; ++j)
            {
                if (m_ListOption[i].isDisplay && m_ListOption[i].isOn)
                {
                    if (m_ListOption[i].Mode == SelectMode.Loop && m_ListOption[i].Index != j)
                    {
                        continue;
                    }
                    selected.Add(m_ListOption[i].Content[j].OptionId);
                }
            }
        }
        int gameId = 0;
        for (int i = 0; i < m_ListGame.Count; ++i)
        {
            if (m_ListGame[i].isOn && m_ListGame[i].isActiveAndEnabled)
            {
                gameId = m_ListGame[i].GameId;
                break;
            }
        }

        ClientSendCreateRoom(gameId, selected);
    }

    private void ClientSendCreateRoom(int gameId, List<int> settingIds)
    {
        Game_C2S_CreateRoomProto proto = new Game_C2S_CreateRoomProto();
        proto.settingIdsList = new List<int>();
        for (int i = 0; i < settingIds.Count; ++i)
        {
            proto.settingIdsList.Add(settingIds[i]);
        }
        proto.gameId = gameId;
        DrbComponent.NetworkSystem.Send(proto);
    }
}
