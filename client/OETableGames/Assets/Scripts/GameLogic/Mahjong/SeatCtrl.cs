//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:08:54
//Description ：
//===================================================
using DG.Tweening;
using DrbFramework.Audio;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SeatCtrl : MonoBehaviour
{
    private enum MahjongState
    {
        Normal,
        Hold,
        Show,
    }
    [SerializeField]
    private Grid3D m_HandContainer;
    [SerializeField]
    private Grid3D m_DeskTopContainer;
    [SerializeField]
    private Transform m_DrawContainer;
    [SerializeField]
    private CombinationCtrl[] m_PengCtrls;
    [SerializeField]
    private int m_nSeatPos;
    [SerializeField]
    private Transform m_DiscardContainer;
    [SerializeField]
    private CameraCtrl m_Camera;
    [SerializeField]
    private HandCtrl m_PushHand;
    [SerializeField]
    private HandCtrl m_DiceHand;

    private Tweener m_WallTweener;
    private List<MahjongCtrl> m_LightMaJiang = new List<MahjongCtrl>();
    private List<MahjongCtrl> m_HandMahjong = new List<MahjongCtrl>();
    private List<MahjongCtrl> m_DesktopMahjong = new List<MahjongCtrl>();
    private List<Combination3D> m_UsedMahjong = new List<Combination3D>();
    private MahjongCtrl m_PrevPlayMahjong = null;

    public int SeatPos
    {
        get { return m_nSeatPos; }
        set { m_nSeatPos = value; }
    }

    public Vector3 DiscardPos { get; set; }

    private const float INIT_POKER_ANIMATION_DURATION = 0.2f;

    private Tweener m_HandTween;

    private void Awake()
    {
        m_HandTween = m_HandContainer.transform.DORotate(new Vector3(-75f, 0f, 0f), INIT_POKER_ANIMATION_DURATION, RotateMode.LocalAxisAdd).SetAutoKill(false).Pause();

        //m_PushHand = MahjongManager.Instance.SpawnHand_Tui().GetComponent<HandCtrl>();
        //m_PushHand.gameObject.transform.SetParent(transform);
        //m_PushHand.gameObject.SetActive(false);

        //m_DiceHand = MahjongManager.Instance.SpawnHand_Fang().GetComponent<HandCtrl>();
        //m_DiceHand.gameObject.transform.SetParent(transform);
        //m_DiceHand.gameObject.SetActive(false);
    }

    public void ShowSettle(Seat seat)
    {
        if (seat == null) return;

        for (int i = 0; i < m_HandMahjong.Count; ++i)
        {
            MahjongManager.Instance.DespawnMahjong(m_HandMahjong[i]);
        }
        m_HandMahjong.Clear();

        for (int i = 0; i < seat.MahjongList.Count; ++i)
        {
            MahjongCtrl newMajiang = MahjongManager.Instance.SpawnMahjong(seat.MahjongList[i], false);
            newMajiang.gameObject.transform.SetParent(m_HandContainer.transform);
            newMajiang.Show(false);
            m_HandContainer.gameObject.SetLayer("Peng" + m_nSeatPos);
            m_HandMahjong.Add(newMajiang);
        }
        m_HandContainer.Sort();
        if (seat.HitMahjong != null)
        {
            MahjongCtrl newMajiang = MahjongManager.Instance.SpawnMahjong(seat.HitMahjong, false);
            newMajiang.gameObject.transform.SetParent(m_DrawContainer.transform);
            newMajiang.Show(false);
            m_DrawContainer.gameObject.SetLayer("Peng" + m_nSeatPos);
            m_HandMahjong.Add(newMajiang);
        }

        for (int i = 0; i < m_UsedMahjong.Count; ++i)
        {
            for (int j = 0; j < m_UsedMahjong[i].MahjongList.Count; ++j)
            {
                MahjongManager.Instance.DespawnMahjong(m_UsedMahjong[i].MahjongList[j]);
            }
        }
        for (int i = 0; i < m_PengCtrls.Length; ++i)
        {
            m_PengCtrls[i].Reset();
        }
        m_UsedMahjong.Clear();
        for (int i = 0; i < seat.UsedMahjongGroups.Count; ++i)
        {
            List<MahjongCtrl> lst = new List<MahjongCtrl>();
            for (int j = 0; j < seat.UsedMahjongGroups[i].MahjongList.Count; ++j)
            {
                MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.UsedMahjongGroups[i].MahjongList[j], MahjongHelper.HasMahjong(seat.UsedMahjongGroups[i].MahjongList[j], seat.UniversalList));
                lst.Add(ctrl);
            }
            Combination3D combination = new Combination3D(seat.UsedMahjongGroups[i].CombinationType, seat.UsedMahjongGroups[i].SubType, lst);
            m_UsedMahjong.Add(combination);
            m_PengCtrls[i].SetUI(combination, seat, false);
        }

        SetDrawMahjongState(MahjongState.Normal);
        SetHandMahjongState(MahjongState.Normal);
    }

    public void SetUniversal(List<Mahjong> universal)
    {
        for (int i = 0; i < m_HandMahjong.Count; ++i)
        {
            if (m_HandMahjong[i].Mahjong != null && MahjongHelper.HasMahjong(m_HandMahjong[i].Mahjong, universal))
            {
                m_HandMahjong[i].SetUniversal(true);
            }
        }
    }

    private void SetHandMahjongState(MahjongState state)
    {
        switch (state)
        {
            case MahjongState.Normal:
                m_HandContainer.transform.localEulerAngles = new Vector3(-90, 0, 0);
                break;
            case MahjongState.Hold:
                m_HandContainer.transform.localEulerAngles = new Vector3(-180, 0, 0);
                m_HandContainer.gameObject.SetLayer("Peng" + m_nSeatPos.ToString());
                break;
            case MahjongState.Show:
                m_HandContainer.transform.localEulerAngles = new Vector3(0, 0, 0);
                m_HandContainer.gameObject.SetLayer("Peng" + m_nSeatPos.ToString());
                break;
        }
    }

    private void SetDrawMahjongState(MahjongState state)
    {
        switch (state)
        {
            case MahjongState.Normal:
                m_DrawContainer.transform.localEulerAngles = new Vector3(-90, 0, 0);
                break;
            case MahjongState.Hold:
                m_DrawContainer.transform.localEulerAngles = new Vector3(180, 0, 0);
                m_DrawContainer.gameObject.SetLayer("Peng" + m_nSeatPos.ToString());
                break;
            case MahjongState.Show:
                m_DrawContainer.transform.localEulerAngles = new Vector3(0, 0, 0);
                m_DrawContainer.gameObject.SetLayer("Peng" + m_nSeatPos.ToString());
                break;
        }
    }

    private void PlayAudio(OperationType type, int subType, int gender)
    {
        string audioName = type.ToString().ToLower();
        audioName = gender.ToString() + "_" + audioName;
        audioName = string.Format("mahjong/{0}", audioName);
        AudioInfo info = new AudioInfo();
        //DrbComponent.AudioSystem.PlayAudio(audioName, info);
    }

    private void PlayAudio(int color, int size, int gender)
    {
        string audioName = string.Format("{0}_{1}_{2}", gender, color, size);
        audioName = string.Format("mahjong/{0}", audioName);
        //DrbComponent.AudioSystem.PlayAudio(audioName);
    }

    public List<MahjongCtrl> GetHand()
    {
        return m_HandMahjong;
    }

    public List<Combination3D> GetUsed()
    {
        return m_UsedMahjong;
    }

    public List<MahjongCtrl> GetDesktop()
    {
        return m_DesktopMahjong;
    }

    public List<MahjongCtrl> GetAll()
    {
        List<MahjongCtrl> lst = new List<MahjongCtrl>();
        lst.AddRange(m_HandMahjong);
        lst.AddRange(m_DesktopMahjong);
        for (int i = 0; i < m_UsedMahjong.Count; ++i)
        {
            lst.AddRange(m_UsedMahjong[i].MahjongList);
        }
        return lst;
    }

    public void Init(Room room)
    {
        if (room == null) return;
        if (room.SeatList.Count == 2)
        {
            m_DeskTopContainer.constraintCount = 15;
            m_DeskTopContainer.transform.localPosition = new Vector3(-68f, 5f, 58f);
        }
        Seat seat = null;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            if (room.SeatList[i].Pos == m_nSeatPos)
            {
                seat = room.SeatList[i];
                break;
            }
        }
        if (seat == null) return;

        Reset(room, seat);

        for (int i = 0; i < seat.MahjongList.Count; ++i)
        {
            MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.MahjongList[i], MahjongHelper.HasMahjong(seat.MahjongList[i], seat.UniversalList));
            ctrl.gameObject.SetParentAndReset(m_HandContainer.transform);
            ctrl.gameObject.SetLayer(m_HandContainer.gameObject.layer);
            m_HandMahjong.Add(ctrl);
        }
        if (seat.HitMahjong != null)
        {
            MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.HitMahjong, MahjongHelper.HasMahjong(seat.HitMahjong, seat.UniversalList));
            ctrl.gameObject.SetParentAndReset(m_DrawContainer.transform);
            ctrl.gameObject.SetLayer(m_DrawContainer.gameObject.layer);
            m_HandMahjong.Add(ctrl);
        }
        for (int i = 0; i < seat.DeskTopMahjong.Count; ++i)
        {
            MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.DeskTopMahjong[i], MahjongHelper.HasMahjong(seat.DeskTopMahjong[i], seat.UniversalList));
            ctrl.gameObject.SetParentAndReset(m_DeskTopContainer.transform);
            ctrl.gameObject.SetLayer(m_DeskTopContainer.gameObject.layer);
            m_DesktopMahjong.Add(ctrl);
        }
        m_DeskTopContainer.Sort();
        for (int i = 0; i < seat.UsedMahjongGroups.Count; ++i)
        {
            List<MahjongCtrl> lst = new List<MahjongCtrl>();
            for (int j = 0; j < seat.UsedMahjongGroups[i].MahjongList.Count; ++j)
            {
                MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.UsedMahjongGroups[i].MahjongList[j], MahjongHelper.HasMahjong(seat.UsedMahjongGroups[i].MahjongList[j], seat.UniversalList));
                lst.Add(ctrl);
            }
            Combination3D combination = new Combination3D(seat.UsedMahjongGroups[i].CombinationType, seat.UsedMahjongGroups[i].SubType, lst);
            m_UsedMahjong.Add(combination);
            m_PengCtrls[i].SetUI(combination, seat);
        }

        Sort(seat);
    }

    private void Reset(Room room, Seat seat)
    {
        if (seat.IsPlayer)
        {
            m_DrawContainer.gameObject.SetLayer("PlayerHand");
            m_HandContainer.gameObject.SetLayer("PlayerHand");
        }
        else
        {
            m_DrawContainer.gameObject.SetLayer("Wall" + seat.Pos);
            m_HandContainer.gameObject.SetLayer("Wall" + seat.Pos);
        }

        SetDrawMahjongState(MahjongState.Normal);
        SetHandMahjongState(MahjongState.Normal);
        Reset();
    }

    private void Reset()
    {
        if (m_PrevPlayMahjong != null)
        {
            m_PrevPlayMahjong.CloseTip();
            m_PrevPlayMahjong = null;
        }

        for (int i = 0; i < m_HandMahjong.Count; ++i)
        {
            MahjongManager.Instance.DespawnMahjong(m_HandMahjong[i]);
        }
        m_HandMahjong.Clear();

        for (int i = 0; i < m_DesktopMahjong.Count; ++i)
        {
            MahjongManager.Instance.DespawnMahjong(m_DesktopMahjong[i]);
        }
        m_DesktopMahjong.Clear();

        for (int i = 0; i < m_UsedMahjong.Count; ++i)
        {
            for (int j = 0; j < m_UsedMahjong[i].MahjongList.Count; ++j)
            {
                MahjongManager.Instance.DespawnMahjong(m_UsedMahjong[i].MahjongList[j]);
            }
        }
        m_UsedMahjong.Clear();

        for (int i = 0; i < m_PengCtrls.Length; ++i)
        {
            m_PengCtrls[i].Reset();
        }
    }

    public void Ready(Seat seat)
    {
        if (seat == null) return;
        if (seat.Pos != m_nSeatPos) return;
        Reset();
    }

    public void Begin(Room room)
    {
        if (room == null) return;

        Seat seat = null;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            if (room.SeatList[i].Pos == m_nSeatPos)
            {
                seat = room.SeatList[i];
                break;
            }
        }
        if (seat == null) return;
        Reset(room, seat);
    }

    public void DealMahjong(Mahjong mahjong, bool isUniversal)
    {
        MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(mahjong, isUniversal);
        ctrl.gameObject.SetParentAndReset(m_HandContainer.transform);
        ctrl.gameObject.SetLayer(m_HandContainer.gameObject.layer);
        m_HandMahjong.Add(ctrl);
        m_HandContainer.Sort();
    }

    public IEnumerator PlayDealMahjongAnimation(Seat seat)
    {
        m_HandTween.OnComplete(() =>
        {
            Sort(seat);
            m_HandTween.PlayBackwards();
        }).Restart();
        yield return new WaitForSeconds(INIT_POKER_ANIMATION_DURATION * 2);
    }

    public void Draw(Seat seat)
    {
        if (seat == null) return;
        if (seat.Pos != m_nSeatPos) return;

        MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(seat.HitMahjong, MahjongHelper.HasMahjong(seat.HitMahjong, seat.UniversalList));
        ctrl.gameObject.SetParentAndReset(m_DrawContainer.transform);
        ctrl.gameObject.SetLayer(m_DrawContainer.gameObject.layer);
        m_HandMahjong.Add(ctrl);
    }

    public void Discard(Seat seat, Mahjong discarded)
    {
        if (seat == null) return;

        if (m_PrevPlayMahjong != null)
        {
            m_PrevPlayMahjong.CloseTip();
            m_PrevPlayMahjong = null;
        }

        if (seat.Pos != m_nSeatPos) return;

        Vector3 sourcePos = Vector3.zero;
        for (int i = 0; i < m_HandMahjong.Count; ++i)
        {
            if (m_HandMahjong[i].Mahjong.index == discarded.index)
            {
                sourcePos = m_HandMahjong[i].transform.position;
                MahjongManager.Instance.DespawnMahjong(m_HandMahjong[i]);
                m_HandMahjong.RemoveAt(i);
                break;
            }
        }



        MahjongCtrl majiang = MahjongManager.Instance.SpawnMahjong(discarded, MahjongHelper.ContainMahjong(discarded, seat.UniversalList));
        m_PrevPlayMahjong = majiang;
        m_DesktopMahjong.Add(majiang);
        majiang.gameObject.SetParentAndReset(m_DeskTopContainer.transform);
        majiang.gameObject.SetLayer(m_DeskTopContainer.gameObject.layer);


        majiang.transform.localPosition = m_DeskTopContainer.GetLocalPos(majiang.transform);

        PlayAudio(discarded.color, discarded.number, seat.Gender);

        StartCoroutine(PlayMahjongAnimation(majiang, sourcePos, seat.IsPlayer));
    }

    private IEnumerator PlayMahjongAnimation(MahjongCtrl majiang, Vector3 sourcePos, bool isPlayer)
    {
        Vector3 destPos = majiang.transform.position;
        Vector3 destAng = majiang.transform.eulerAngles;

        majiang.transform.position = sourcePos;
        if (sourcePos == Vector3.zero)
        {
            sourcePos = m_DrawContainer.transform.position;
        }
        if (isPlayer && DiscardPos != Vector3.zero)
        {
            sourcePos = DiscardPos;
            DiscardPos = Vector3.zero;
        }
        if (isPlayer)
        {
            Vector3 screenPos = m_Camera.HandMahjongCamera.WorldToViewportPoint(sourcePos);
            sourcePos = m_Camera.MainCamera.ViewportToWorldPoint(screenPos + new Vector3(0f, 0f, 300f));
        }


        AnimationCurve curve = new AnimationCurve(new Keyframe[] { new Keyframe(0f, 0f), new Keyframe(0.3f, 1f), new Keyframe(1f, 1f) });
        const float animationTime = 0.5f;
        majiang.transform.DOScale(1.8f, animationTime).SetEase(curve);
        majiang.transform.position = m_Camera.transform.position + m_Camera.transform.forward;

        majiang.transform.LookAt(m_Camera.transform.position, Vector3.up);
        majiang.transform.eulerAngles += new Vector3(-75, 0, 180);
        majiang.transform.position = sourcePos;
        majiang.transform.DOMove(m_DiscardContainer.transform.position, animationTime).SetEase(curve).OnComplete(() =>
        {
            majiang.transform.DOMove(destPos, 0.15f).OnComplete(() =>
            {
                //AudioEffectManager.Instance.Play("chupai");
                if (majiang == m_PrevPlayMahjong)
                {
                    majiang.ShowTip(false);
                }
            });
            majiang.transform.DORotate(destAng, 0.15f);
            majiang.transform.DOScale(1.0f, 0.15f);
        });
        yield return null;
    }

    public void Operate(Seat seat, MahjongGroup combination)
    {
        if (seat == null) return;
        if (combination == null) return;

        for (int i = 0; i < combination.MahjongList.Count; ++i)
        {
            for (int j = 0; j < m_DesktopMahjong.Count; ++j)
            {
                if (m_DesktopMahjong[j].Mahjong.index == combination.MahjongList[i].index)
                {
                    MahjongManager.Instance.DespawnMahjong(m_DesktopMahjong[j]);
                    m_DesktopMahjong.RemoveAt(j);
                    break;
                }
            }
            for (int j = 0; j < m_HandMahjong.Count; ++j)
            {
                if (m_HandMahjong[j].Mahjong.index == combination.MahjongList[i].index)
                {
                    MahjongManager.Instance.DespawnMahjong(m_HandMahjong[j]);
                    m_HandMahjong.RemoveAt(j);
                    break;
                }
            }
            for (int j = m_UsedMahjong.Count - 1; j >= 0; --j)
            {
                for (int k = 0; k < m_UsedMahjong[j].MahjongList.Count; ++k)
                {
                    if (m_UsedMahjong[j].MahjongList[k].Mahjong.index == combination.MahjongList[i].index)
                    {
                        MahjongManager.Instance.DespawnMahjong(m_UsedMahjong[j].MahjongList[k]);
                        m_UsedMahjong[j].MahjongList.RemoveAt(k);
                        break;
                    }
                }
            }
        }

        if (m_PrevPlayMahjong != null)
        {
            m_PrevPlayMahjong.CloseTip();
            m_PrevPlayMahjong = null;
        }

        if (seat.Pos != m_nSeatPos) return;

        for (int i = 0; i < m_PengCtrls.Length; ++i)
        {
            if (m_PengCtrls[i].Combination == null) continue;
            if (m_PengCtrls[i].Combination.MahjongList.Count == 0)
            {
                m_PengCtrls[i].Reset();
            }
        }

        PlayAudio(combination.CombinationType, combination.SubType, seat.Gender);

        List<MahjongCtrl> lst = new List<MahjongCtrl>();

        for (int i = 0; i < combination.MahjongList.Count; ++i)
        {
            MahjongCtrl ctrl = MahjongManager.Instance.SpawnMahjong(combination.MahjongList[i], MahjongHelper.HasMahjong(combination.MahjongList[i], seat.UniversalList));
            lst.Add(ctrl);
        }

        Combination3D combination3D = new Combination3D(combination.CombinationType, combination.SubType, lst);
        m_UsedMahjong.Add(combination3D);

        for (int i = 0; i < m_PengCtrls.Length; ++i)
        {
            if (m_PengCtrls[i].Combination == null)
            {
                m_PengCtrls[i].SetUI(combination3D, seat);
                break;
            }
        }
    }

    public void Hu(Seat seat, bool isZiMo)
    {
        if (seat == null) return;
        MahjongCtrl ctrl = null;
        if (seat.HitMahjong != null)
        {
            for (int i = 0; i < m_DesktopMahjong.Count; ++i)
            {
                if (m_DesktopMahjong[i].Mahjong.index == seat.HitMahjong.index)
                {
                    ctrl = m_DesktopMahjong[i];
                    break;
                }
            }

            for (int i = 0; i < m_HandMahjong.Count; ++i)
            {
                if (m_HandMahjong[i].Mahjong.index == seat.HitMahjong.index)
                {
                    ctrl = m_HandMahjong[i];
                    break;
                }
            }
            for (int i = 0; i < m_UsedMahjong.Count; ++i)
            {
                for (int j = 0; j < m_UsedMahjong[i].MahjongList.Count; ++j)
                {
                    if (m_UsedMahjong[i].MahjongList[j].Mahjong.index == seat.HitMahjong.index)
                    {
                        ctrl = m_UsedMahjong[i].MahjongList[j];
                        break;
                    }
                }
            }
        }

        if (ctrl != null)
        {
            //Transform effect = EffectManager.Instance.PlayEffect("zimo", 4f);
            //effect.transform.SetParent(ctrl.transform.parent);
            //effect.gameObject.SetLayer(ctrl.gameObject.layer);
            //effect.position = ctrl.transform.position;
            //effect.eulerAngles = Vector3.zero;
        }
        if (seat.Pos != m_nSeatPos) return;
        PlayAudio(isZiMo ? OperationType.ZiMo : OperationType.Hu, 0, seat.Gender);
    }

    public void Sort(Seat seat)
    {
        if (seat == null) return;

        MahjongHelper.UniversalBestSort(seat.MahjongList, seat.UniversalList);

        if (seat.Pos != m_nSeatPos) return;

        for (int i = 0; i < seat.MahjongList.Count; ++i)
        {
            for (int j = 0; j < m_HandMahjong.Count; ++j)
            {
                if (m_HandMahjong[j].Mahjong.index == seat.MahjongList[i].index)
                {
                    m_HandMahjong[j].gameObject.SetParentAndReset(m_HandContainer.transform);
                    m_HandMahjong[j].gameObject.SetLayer(m_HandContainer.gameObject.layer);
                    m_HandMahjong[j].transform.SetSiblingIndex(i);
                    break;
                }
            }
        }
        if (seat.HitMahjong != null)
        {
            for (int i = 0; i < m_HandMahjong.Count; ++i)
            {
                if (m_HandMahjong[i].Mahjong.index == seat.HitMahjong.index)
                {
                    m_HandMahjong[i].gameObject.SetParentAndReset(m_DrawContainer.transform);
                    m_HandMahjong[i].gameObject.SetLayer(m_DrawContainer.gameObject.layer);
                    break;
                }
            }
        }
        m_HandContainer.Sort();
    }

    public void CheckTing(Seat seat)
    {
        if (seat == null || seat.Pos != m_nSeatPos) return;

        for (int i = 0; i < m_LightMaJiang.Count; ++i)
        {
            m_LightMaJiang[i].CloseTip();
        }
        m_LightMaJiang.Clear();

        Dictionary<Mahjong, List<Mahjong>> dic = seat.TingDic;
        if (dic != null && dic.Count > 0)
        {
            foreach (var pair in dic)
            {
                for (int i = 0; i < m_HandMahjong.Count; ++i)
                {
                    if (pair.Key.index == m_HandMahjong[i].Mahjong.index)
                    {
                        m_HandMahjong[i].ShowTip(true);
                        m_LightMaJiang.Add(m_HandMahjong[i]);
                        break;
                    }
                }
            }
        }
    }
}
