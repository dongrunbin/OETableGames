//===================================================
//Author      : DRB
//CreateTime  ：2021/4/22 0:51:46
//Description ：
//===================================================
using DG.Tweening;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class MahjongLogic : MonoBehaviour
{
    [SerializeField]
    private SeatCtrl[] m_Seats;
    [SerializeField]
    private Transform[] m_DiceContainer;
    [SerializeField]
    private Transform m_DiceHandContainer;
    [SerializeField]
    private Transform m_EffectContainer;
    [SerializeField]
    private CompassCtrl m_CompassCtrl;
    [SerializeField]
    private CameraCtrl m_CameraCtrl;
    [SerializeField]
    private Grid3D[] m_WallContainers;
    [SerializeField]
    private Transform m_WallsParent;
    private Tweener m_WallAnimation;
    private const float DEAL_ANIMATION_DURATION = 0.02f;
    private const float CREATE_WALL_ANIMATION_DURATION = 0.3f;
    private bool m_isPlayingAnimation;
    private Vector2 m_BeginScreenPos;
    private bool m_isDraging;

    private List<MahjongCtrl> m_SelectMahjong = new List<MahjongCtrl>();
    private List<MahjongCtrl> m_Wall = new List<MahjongCtrl>();
    private List<MahjongCtrl> m_WallInverse = new List<MahjongCtrl>();

    public Action<Mahjong> OnDoubleClickMahjong;
    public Action<Mahjong> OnSelectMahjong;

    private class OperationInfo
    {
        public enum OperationType
        {
            Draw,
            Discard,
            Operation,
            Settle,
        }
        public OperationType type;
        public Seat seat;
        public Mahjong mahjong;
        public bool isFromLast;
        public Room room;
        public MahjongGroup group;

        public OperationInfo(Room room)
        {
            this.type = OperationType.Settle;
            this.room = room;
        }
        public OperationInfo(Seat seat, Mahjong mahjong)
        {
            this.type = OperationType.Discard;
            this.seat = seat;
            this.mahjong = mahjong;
        }
        public OperationInfo(Seat seat, Mahjong mahjong, bool isFromLast)
        {
            this.type = OperationType.Draw;
            this.seat = seat;
            this.mahjong = mahjong;
            this.isFromLast = isFromLast;
        }
        public OperationInfo(Seat seat, MahjongGroup group)
        {
            this.type = OperationType.Operation;
            this.seat = seat;
            this.group = group;
        }
    }

    private Queue<OperationInfo> m_OperationQueue = new Queue<OperationInfo>();

    private void Awake()
    {
        if (FingerEvent.Instance != null)
        {
            FingerEvent.Instance.OnPlayerClickUp += OnPlayerClickUp;
            FingerEvent.Instance.OnPlayerClickDown += OnPlayerClickDown;
            FingerEvent.Instance.OnFingerDrag += OnFingerDrag;
        }
    }

    private void OnDestroy()
    {
        if (FingerEvent.Instance != null)
        {
            FingerEvent.Instance.OnPlayerClickUp -= OnPlayerClickUp;
            FingerEvent.Instance.OnPlayerClickDown -= OnPlayerClickDown;
            FingerEvent.Instance.OnFingerDrag -= OnFingerDrag;
        }
    }

    public void Init(Room room)
    {
        if (room.SeatList.Count == 2 && m_Seats.Length == 4)
        {
            m_Seats[1].SeatPos = 3;
            m_Seats = new SeatCtrl[2] { m_Seats[0], m_Seats[2] };
            m_Seats[1].SeatPos = 2;
        }
        for (int i = 0; i < m_Seats.Length; ++i)
        {
            m_Seats[i].Init(room);
        }

        m_SelectMahjong.Clear();

        m_CompassCtrl.Init(room.SeatList.Count);
        m_CameraCtrl.SetPos(room.PlayerSeat.Pos, room.SeatList.Count);

        if (room.RoomStatus == RoomStatus.Waiting) return;

        RebuildWall(room);

        int gangCount = 0;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            for (int j = 0; j < room.SeatList[i].UsedMahjongGroups.Count; ++j)
            {
                if (room.SeatList[i].UsedMahjongGroups[j].CombinationType == OperationType.Kong)
                {
                    ++gangCount;
                }
            }
        }

        for (int i = 0; i < gangCount; ++i)
        {
            MahjongCtrl mj = m_WallInverse[0];
            MahjongManager.Instance.DespawnMahjong(mj);
            m_WallInverse.Remove(mj);
            m_Wall.Remove(mj);
        }

        int usedCount = room.MahjongTotal - room.MahjongAmount - gangCount;
        for (int i = 0; i < usedCount; ++i)
        {
            MahjongCtrl mj = m_Wall[0];
            MahjongManager.Instance.DespawnMahjong(mj);
            m_WallInverse.Remove(mj);
            m_Wall.Remove(mj);
        }

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            if (room.SeatList[i].Status == SeatStatus.Discard)
            {
                m_CompassCtrl.SetCurrent(room.SeatList[i].Pos);
                break;
            }
        }
    }

    public void Enter(Seat seat)
    {

    }

    public void Ready(Seat seat)
    {
        if (!seat.IsPlayer) return;
        GetSeatCtrlBySeatPos(seat.Pos).Ready(seat);
        Reset();
    }

    public void Disband()
    {

    }

    public void Begin(Room room)
    {
        if (room == null) return;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            GetSeatCtrlBySeatPos(room.SeatList[i].Pos).Begin(room);
        }
        m_SelectMahjong.Clear();
        m_CompassCtrl.SetNormal();
        RebuildWall(room);
        m_isPlayingAnimation = true;
        StartCoroutine(BeginAnimation(room));
        StartCoroutine(Delay());
    }

    public void Draw(Seat seat, Mahjong mahjong, bool isFromLast)
    {
        if (seat == null) return;

        if (m_isPlayingAnimation)
        {
            m_OperationQueue.Enqueue(new OperationInfo(seat, mahjong, isFromLast));
            return;
        }

        MahjongCtrl mj = isFromLast ? m_WallInverse[0] : m_Wall[0];
        m_WallInverse.Remove(mj);
        m_Wall.Remove(mj);
        MahjongManager.Instance.DespawnMahjong(mj);
        m_CompassCtrl.SetCurrent(seat.Pos);
        GetSeatCtrlBySeatPos(seat.Pos).Draw(seat, mahjong);
        GetSeatCtrlBySeatPos(seat.Pos).CheckTing(seat);

        m_CompassCtrl.SetCurrent(seat.Pos);
    }

    private IEnumerator Delay()
    {
        while (m_isPlayingAnimation)
        {
            yield return null;
        }
        while (m_OperationQueue.Count > 0)
        {
            OperationInfo info = m_OperationQueue.Dequeue();
            switch (info.type)
            {
                case OperationInfo.OperationType.Draw:
                    Draw(info.seat, info.mahjong, info.isFromLast);
                    break;
                case OperationInfo.OperationType.Discard:
                    Discard(info.seat, info.mahjong);
                    break;
                case OperationInfo.OperationType.Operation:
                    Operation(info.seat, info.group);
                    break;
                case OperationInfo.OperationType.Settle:
                    Settle(info.room);
                    break;
            }
        }
    }

    public void Discard(Seat seat, Mahjong mahjong)
    {
        if (m_isPlayingAnimation)
        {
            m_OperationQueue.Enqueue(new OperationInfo(seat, mahjong));
            return;
        }

        SeatCtrl ctrl = GetSeatCtrlBySeatPos(seat.Pos);
        for (int i = 0; i < m_Seats.Length; ++i)
        {
            m_Seats[i].Discard(seat, mahjong);
        }

        ctrl.Sort(seat);
    }

    public void AskOperation(List<MahjongGroup> lst)
    {

    }

    public void Operation(Seat seat, MahjongGroup group)
    {
        if (m_isPlayingAnimation)
        {
            m_OperationQueue.Enqueue(new OperationInfo(seat, group));
            return;
        }

        for (int i = 0; i < m_Seats.Length; ++i)
        {
            m_Seats[i].Operate(seat, group);
        }
        GetSeatCtrlBySeatPos(seat.Pos).Sort(seat);

        m_CompassCtrl.SetCurrent(seat.Pos);
    }

    public void Pass()
    {

    }

    public void CheckTing(Seat seat)
    {
        GetSeatCtrlBySeatPos(seat.Pos).CheckTing(seat);
    }

    public void Settle(Room room)
    {
        if (room == null) return;

        if (m_isPlayingAnimation)
        {
            m_OperationQueue.Enqueue(new OperationInfo(room));
            return;
        }

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            GetSeatCtrlBySeatPos(room.SeatList[i].Pos).Settle(room.SeatList[i]);
        }

        DrbComponent.AudioSystem.PlaySoundEffect(room.PlayerSeat.isWiner ? "mahjong_win" : "mahjong_lose");
    }

    private SeatCtrl GetSeatCtrlBySeatPos(int seatPos)
    {
        for (int i = 0; i < m_Seats.Length; ++i)
        {
            if (m_Seats[i].SeatPos == seatPos)
            {
                return m_Seats[i];
            }
        }
        return null;
    }

    public IEnumerator RollDice(int seatPos, int DiceA, int DiceB)
    {
        if (seatPos == 0) yield break;
        if (DiceA == 0 && DiceB == 0) yield break;
        GameObject hand = MahjongManager.Instance.SpawnDiceHand();
        hand.SetParentAndReset(m_DiceHandContainer);
        hand.transform.localEulerAngles = new Vector3(0, (seatPos - 1) * -90f, 0);
        yield return new WaitForSeconds(0.5f);

        DrbComponent.AudioSystem.PlaySoundEffect("mahjong_rolldice");

        Coroutine coroutine = null;
        if (DiceA != 0)
        {
            GameObject dice1 = MahjongManager.Instance.SpawnDice();
            DiceCtrl ctrl = dice1.GetComponent<DiceCtrl>();
            dice1.SetParentAndReset(m_DiceContainer[0]);
            dice1.transform.localPosition = GameUtil.GetRandomPos(dice1.transform.position, 1f);
            coroutine = StartCoroutine(ctrl.RollAnimation(DiceA));
        }

        if (DiceB != 0)
        {
            GameObject dice2 = MahjongManager.Instance.SpawnDice();
            DiceCtrl ctrl2 = dice2.GetComponent<DiceCtrl>();
            dice2.SetParentAndReset(m_DiceContainer[1]);
            dice2.transform.localPosition = GameUtil.GetRandomPos(dice2.transform.position, 1f);
            coroutine = StartCoroutine(ctrl2.RollAnimation(DiceB));
        }
        yield return coroutine;
    }

    private void OnPlayerClickDown(Vector2 screenPos)
    {
        if (EventSystem.current.IsPointerOverGameObject())
        {
            return;
        }

        Ray ray = m_CameraCtrl.MainCamera.ScreenPointToRay(Input.mousePosition);
        RaycastHit[] hitArr = Physics.RaycastAll(ray, Mathf.Infinity, 1 << LayerMask.NameToLayer("Table"));
        if (hitArr.Length > 0)
        {
            MahjongCtrl ctrl = hitArr[0].collider.gameObject.GetComponent<MahjongCtrl>();
            if (ctrl == null) return;
        }

        ray = m_CameraCtrl.HandMahjongCamera.ScreenPointToRay(Input.mousePosition);
        hitArr = Physics.RaycastAll(ray, Mathf.Infinity, 1 << LayerMask.NameToLayer("PlayerHand"));
        if (hitArr.Length > 0)
        {
            MahjongCtrl ctrl = hitArr[0].collider.gameObject.GetComponent<MahjongCtrl>();
            if (ctrl == null) return;
            DrbComponent.AudioSystem.PlaySoundEffect("mahjong_click");
            if (ctrl == null || ctrl.Mahjong == null) return;

            if (m_SelectMahjong.Contains(ctrl))
            {
                ctrl.isSelect = false;
                m_SelectMahjong.Remove(ctrl);

                if (OnDoubleClickMahjong != null)
                {
                    OnDoubleClickMahjong(ctrl.Mahjong);
                }
            }
            else
            {
                for (int i = 0; i < m_SelectMahjong.Count; ++i)
                {
                    m_SelectMahjong[i].isSelect = false;
                }
                m_SelectMahjong.Clear();
                ctrl.isSelect = true;
                m_SelectMahjong.Add(ctrl);
                m_BeginScreenPos = screenPos;
            }
            if (m_SelectMahjong.Count > 0)
            {
                if (OnSelectMahjong != null)
                {
                    OnSelectMahjong(m_SelectMahjong[0].Mahjong);
                }
            }


            List<MahjongCtrl> allMahjong = new List<MahjongCtrl>();
            for (int i = 0; i < m_Seats.Length; ++i)
            {
                List<Combination3D> lstUsed = m_Seats[i].GetUsed();
                for (int j = 0; j < lstUsed.Count; ++j)
                {
                    allMahjong.AddRange(lstUsed[j].MahjongList);
                }
                allMahjong.AddRange(m_Seats[i].GetDesktop());
            }
            for (int i = 0; i < allMahjong.Count; ++i)
            {
                allMahjong[i].ForwardColor = MahjongCtrl.ForwardColorType.Normal;
            }

            if (m_SelectMahjong.Count > 0)
            {
                for (int i = 0; i < allMahjong.Count; ++i)
                {
                    if (allMahjong[i].Mahjong.Equals(ctrl.Mahjong))
                    {
                        allMahjong[i].ForwardColor = MahjongCtrl.ForwardColorType.Gray;
                    }
                }
            }
        }
    }

    private void OnPlayerClickUp(Vector2 screenPos)
    {
        if (m_SelectMahjong == null || m_SelectMahjong.Count != 1) return;
        MahjongCtrl ctrl = m_SelectMahjong[0];

        if (m_BeginScreenPos != Vector2.zero && screenPos.y - m_BeginScreenPos.y > Screen.currentResolution.height * 0.08f)
        {
            if (OnDoubleClickMahjong != null)
            {
                OnDoubleClickMahjong(ctrl.Mahjong);
            }
        }
        else
        {
            ctrl.isSelect = true;
        }
        m_BeginScreenPos = Vector2.zero;
        m_isDraging = false;
    }

    private void OnFingerDrag(Vector2 screenPos)
    {
        if (m_SelectMahjong == null || m_SelectMahjong.Count != 1) return;

        if (screenPos.y - m_BeginScreenPos.y > Screen.currentResolution.height * 0.08f)
        {
            m_isDraging = true;
        }

        if (m_isDraging)
        {
            MahjongCtrl ctrl = m_SelectMahjong[0];
            Camera camera = m_CameraCtrl.HandMahjongCamera;
            Vector3 worldPos = camera.ScreenToWorldPoint(new Vector3(screenPos.x, screenPos.y, 10f));
            ctrl.Model.transform.position = worldPos;
            for (int i = 0; i < m_Seats.Length; ++i)
            {
                m_Seats[i].DiscardPos = worldPos;
            }
        }
        else
        {
            Ray ray = m_CameraCtrl.HandMahjongCamera.ScreenPointToRay(Input.mousePosition);
            RaycastHit[] hitArr = Physics.RaycastAll(ray, Mathf.Infinity, 1 << LayerMask.NameToLayer("PlayerHand"));

            if (hitArr.Length > 0)
            {
                MahjongCtrl ctrl = hitArr[0].collider.gameObject.GetComponent<MahjongCtrl>();
                if (ctrl != null)
                {
                    m_SelectMahjong[0].isSelect = false;
                    m_SelectMahjong.Clear();
                    ctrl.isSelect = true;
                    m_SelectMahjong.Add(ctrl);
                }
            }
        }
    }

    public void ClearSelectedMahjongs()
    {
        for (int i = 0; i < m_SelectMahjong.Count; ++i)
        {
            m_SelectMahjong[i].ForwardColor = MahjongCtrl.ForwardColorType.Normal;
            m_SelectMahjong[i].isSelect = false;
        }
        m_SelectMahjong.Clear();
    }

    private void Reset()
    {
        for (int i = 0; i < m_Wall.Count; ++i)
        {
            MahjongManager.Instance.DespawnMahjong(m_Wall[i]);
        }
        m_Wall.Clear();
        m_WallInverse.Clear();
    }

    private void RebuildWall(Room room)
    {
        Reset();
        int diceTotal = room.FirstDice.diceTotal;
        diceTotal += room.SecondDice.diceTotal;

        int pos = room.BankerPos;
        for (int i = 1; i < diceTotal; ++i)
        {
            ++pos;
            if (pos > 4)
            {
                pos -= 4;
            }
        }

        int fromWallIndex = room.MahjongTotal / 4 * (pos - 1) + diceTotal * 2;
        if (fromWallIndex % 2 == 1)
        {
            fromWallIndex += 1;
        }

        for (int i = 0; i < room.MahjongTotal; ++i)
        {
            MahjongCtrl mj = MahjongManager.Instance.SpawnMahjong(null, false);
            m_Wall.Add(mj);
        }
        int playerCount = m_WallContainers.Length;
        int tableMaJiangCount = m_Wall.Count / playerCount;
        int index = 0;
        if (tableMaJiangCount % 2 == 0)
        {
            for (int i = 0; i < m_WallContainers.Length; ++i)
            {
                int endIndex = tableMaJiangCount + index;
                for (int j = index; j < endIndex; ++j)
                {
                    m_Wall[j].gameObject.SetParentAndReset(m_WallContainers[i].transform);
                    m_Wall[j].gameObject.SetLayer(m_WallContainers[i].gameObject.layer);
                }
                index = endIndex;
                m_WallContainers[i].Sort();
            }
        }
        else
        {
            for (int i = 0; i < m_WallContainers.Length; ++i)
            {
                int endIndex = 0;
                if (i % 2 == 0)
                {
                    endIndex = tableMaJiangCount + index + 1;
                }
                else
                {
                    endIndex = tableMaJiangCount + index - 1;
                }
                for (int j = index; j < endIndex; ++j)
                {
                    m_Wall[j].gameObject.SetParentAndReset(m_WallContainers[i].transform);
                    m_Wall[j].gameObject.SetLayer(m_WallContainers[i].gameObject.layer);
                }
                index = endIndex;
                m_WallContainers[i].Sort();
            }
        }

        for (int i = fromWallIndex, j = 0; i < m_Wall.Count; ++i, ++j)
        {
            MahjongCtrl mj = m_Wall[i];
            m_Wall.Remove(mj);
            m_Wall.Insert(j, mj);
        }

        for (int i = m_Wall.Count - 2; i >= 0; i -= 2)
        {
            m_WallInverse.Add(m_Wall[i]);
            m_WallInverse.Add(m_Wall[i + 1]);
        }

        for (int i = 0; i < m_WallContainers.Length; ++i)
        {
            m_WallContainers[i].Sort();
        }
    }

    private IEnumerator BeginAnimation(Room room)
    {
        yield return PlayCreateWallAnimation();
        yield return StartCoroutine(RollDice(room.BankerPos, room.FirstDice.diceA, room.FirstDice.diceB));
        yield return StartCoroutine(RollDice(room.BankerPos, room.SecondDice.diceA, room.SecondDice.diceB));
        yield return StartCoroutine(PlayDealAnimation(room));
        m_isPlayingAnimation = false;
    }

    private IEnumerator PlayCreateWallAnimation()
    {
        Vector3 destPoint = m_WallsParent.transform.position;
        m_WallAnimation = m_WallsParent.DOMove(destPoint, CREATE_WALL_ANIMATION_DURATION).SetEase(Ease.Linear).SetAutoKill(false).Pause();
        for (int i = 0; i < m_Wall.Count; ++i)
        {
            m_Wall[i].gameObject.SetActive(false);
            m_Wall[i].transform.position = m_Wall[i].transform.position + new Vector3(0, -20, 0);
        }

        yield return null;

        m_WallAnimation.OnComplete(() =>
        {
            for (int i = 0; i < m_Wall.Count; ++i)
            {
                m_Wall[i].gameObject.SetActive(true);
                m_Wall[i].gameObject.transform.DOMove(m_Wall[i].transform.position - new Vector3(0, -20, 0), CREATE_WALL_ANIMATION_DURATION).SetEase(Ease.Linear).SetAutoKill(true);
            }

            m_WallAnimation.PlayBackwards();
        }).Restart();

        yield return new WaitForSeconds(CREATE_WALL_ANIMATION_DURATION * 2);
    }

    private IEnumerator PlayDealAnimation(Room room)
    {
        //AudioEffectManager.Instance.Play("fapai", Vector3.zero, false);
        const int countPerTimes = 4;
        int mahjongCount = room.SeatList[0].MahjongList.Count;
        if (mahjongCount == 0) yield break;
        int loopCount = Mathf.FloorToInt(mahjongCount / countPerTimes);
        int overplusCount = mahjongCount % countPerTimes;

        for (int i = 0; i < loopCount; ++i)
        {
            for (int j = 0; j < room.SeatList.Count; ++j)
            {
                Seat seat = room.SeatList[j];
                for (int k = 0; k < countPerTimes; ++k)
                {
                    int index = i * countPerTimes + k;
                    if (index >= seat.MahjongList.Count) continue;
                    GetSeatCtrlBySeatPos(seat.Pos).DealMahjong(seat.MahjongList[index], MahjongHelper.CheckUniversal(seat.MahjongList[index], seat.UniversalList));
                    MahjongCtrl mj = m_Wall[0];

                    MahjongManager.Instance.DespawnMahjong(mj);
                    m_Wall.Remove(mj);
                    m_WallInverse.Remove(mj);
                }
                yield return new WaitForSeconds(DEAL_ANIMATION_DURATION);
            }
        }

        yield return null;

        for (int j = 0; j < room.SeatList.Count; ++j)
        {
            Seat seat = room.SeatList[j];
            for (int k = 0; k < overplusCount; ++k)
            {
                int index = loopCount * countPerTimes + k;
                if (index >= seat.MahjongList.Count) continue;
                GetSeatCtrlBySeatPos(seat.Pos).DealMahjong(seat.MahjongList[index], MahjongHelper.CheckUniversal(seat.MahjongList[index], seat.UniversalList));
                MahjongCtrl mj = m_Wall[0];
                MahjongManager.Instance.DespawnMahjong(mj);
                m_Wall.Remove(mj);
                m_WallInverse.Remove(mj);
            }
            yield return new WaitForSeconds(DEAL_ANIMATION_DURATION);
        }
        yield return null;

        yield return StartCoroutine(GetSeatCtrlBySeatPos(room.PlayerSeat.Pos).PlayDealMahjongAnimation(room.PlayerSeat));
    }
}
