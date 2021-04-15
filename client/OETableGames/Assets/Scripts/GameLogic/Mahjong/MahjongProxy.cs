//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:50:37
//Description ：
//===================================================

using System.Collections.Generic;

public class MahjongProxy
{
    public Room Room;

    private int MaxSeatCount
    {
        get
        {
            return 4;
        }
    }

    public MahjongProxy(Room room)
    {
        Room = room;

        CalculateSeatIndex();
    }

    private void CalculateSeatIndex()
    {
        if (Room == null) return;
        int playerPos = 1;
        List<Seat> seats = Room.SeatList;
        for (int i = 0; i < seats.Count; ++i)
        {
            if (seats[i].IsPlayer)
            {

                playerPos = seats[i].Pos;
                break;
            }
        }
        for (int j = 0; j < seats.Count; ++j)
        {
            Seat seat = seats[j];
            int seatIndex = seat.Pos - playerPos;
            seatIndex = seatIndex < 0 ? seatIndex + MaxSeatCount : seatIndex;
            if (seats.Count == 2 && seatIndex != 0)
            {
                seatIndex = 1;
            }
            seat.Index = seatIndex;
        }
    }

    public void EnterRoom(Seat tempSeat)
    {
        if (Room == null) return;
        Seat seat = GetSeatBySeatId(tempSeat.Pos);
        if (seat == null) return;
        seat.Init(tempSeat);
    }

    public void ExitRoom(int playerId)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.Clear();
    }

    public void Ready(int playerId)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        Room.RoomStatus = RoomStatus.Waiting;
        seat.Status = SeatStatus.Ready;
    }

    public void CostGold(int playerId, int gold)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.Gold -= gold;
    }

    public void ApplyDisband(int playerId, DisbandStatus state, long disbandTime, int maxDisbandTime)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.DisbandState = state;
        Room.DisbandTimeMax = maxDisbandTime;
        if (disbandTime != 0)
        {
            Room.DisbandTime = disbandTime;
        }
    }

    public void Disband(bool isDisband)
    {
        if (Room == null) return;
        Room.isDisband = isDisband;
    }

    private struct TingArgs
    {
        public int pengCount;
        public int gangCount;
        public int wanCount;
        public int tongCount;
        public int tiaoCount;
        public int fengCount;
        public int ziCount;
        public int hongzhongCount;
        public int universalCount;
    }

    public void CalculatePokerCount(bool containHandPoker)
    {
        if (Room == null) return;
        if (Room.SeatList == null) return;
        Room.PokerCount.Clear();
        for (int i = 1; i < 8; ++i)
        {
            for (int j = 1; j < 10; ++j)
            {
                if (i == 5 && j > 3) break;
                if ((i == 4 || i > 5) && j > 4) break;
                int hash = string.Format("{0}_{1}", i, j).GetHashCode();
                Room.PokerCount[hash] = 0;
            }
        }

        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            Seat seat = Room.SeatList[i];
            if (containHandPoker)
            {
                for (int j = 0; j < seat.PokerList.Count; ++j)
                {
                    int hash = seat.PokerList[j].GetHashCode();
                    if (!Room.PokerCount.ContainsKey(hash)) continue;
                    ++Room.PokerCount[hash];
                }
                if (seat.HitPoker != null)
                {
                    int hash = seat.HitPoker.GetHashCode();
                    if (!Room.PokerCount.ContainsKey(hash)) continue;
                    ++Room.PokerCount[hash];
                }
            }
            for (int j = 0; j < seat.DeskTopPoker.Count; ++j)
            {
                int hash = seat.DeskTopPoker[j].GetHashCode();
                if (!Room.PokerCount.ContainsKey(hash)) continue;
                ++Room.PokerCount[hash];
            }
            for (int j = 0; j < seat.UsedPokerList.Count; ++j)
            {
                for (int k = 0; k < seat.UsedPokerList[j].MahjongList.Count; ++k)
                {
                    int hash = seat.UsedPokerList[j].MahjongList[k].GetHashCode();
                    if (!Room.PokerCount.ContainsKey(hash)) continue;
                    ++Room.PokerCount[hash];
                }
            }
        }
    }

    public void Begin(Room room)
    {
        if (room == null) return;
        if (Room == null) return;
        Room.RoomStatus = RoomStatus.Gaming;
        Room.PokerTotal = room.PokerTotal;
        Room.currentLoop = room.currentLoop;
        Room.PokerAmount = room.PokerAmount;
        Room.FirstDice = room.FirstDice;
        Room.SecondDice = room.SecondDice;
        Room.PlayedTimes = 0;
        Room.PokerCount.Clear();

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            Seat tempSeat = room.SeatList[i];
            Seat entity = GetSeatBySeatId(tempSeat.Pos);
            entity.Reset();
            entity.PokerList.AddRange(tempSeat.PokerList);
            entity.UniversalList.AddRange(tempSeat.UniversalList);
            entity.PokerAmount = tempSeat.PokerAmount;
            entity.IsBanker = tempSeat.IsBanker;
        }
    }

    public void Draw(int playerId, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;

        if (seat.HitPoker != null)
        {
            seat.PokerList.Add(seat.HitPoker);
            seat.HitPoker = mahjong;
        }
        else
        {
            seat.HitPoker = mahjong;
        }

        seat.Status = SeatStatus.PlayPoker;
    }

    public void Discard(int playerId, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        ++Room.PlayedTimes;
        seat.Status = SeatStatus.Wait;
        CalculatePokerCount(false);
        ++seat.playedTimes;

        if (seat.HitPoker != null)
        {
            if (seat.HitPoker.index == mahjong.index)
            {
                seat.HitPoker = null;
            }
            else
            {
                for (int i = 0; i < seat.PokerList.Count; ++i)
                {
                    if (seat.PokerList[i].index == mahjong.index)
                    {
                        seat.PokerList.Remove(seat.PokerList[i]);
                        break;
                    }
                }
                seat.PokerList.Add(seat.HitPoker);
                seat.HitPoker = null;
            }
        }
        else
        {
            for (int i = 0; i < seat.PokerList.Count; ++i)
            {
                if (seat.PokerList[i].index == mahjong.index)
                {
                    seat.PokerList.Remove(seat.PokerList[i]);
                    break;
                }
            }
        }
        seat.DeskTopPoker.Add(mahjong);
    }

    public void AskOperation(List<MahjongGroup> lst)
    {
        if (Room == null) return;
        if (Room.PlayerSeat == null) return;
        Room.AskPokerGroup = lst;
        Room.PlayerSeat.Status = SeatStatus.Operate;
    }

    public void OperatePoker(OperationType type, int playerId, int subType, List<Mahjong> lst)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.Status = SeatStatus.PlayPoker;

        MahjongGroup conbination = null;
        if (lst != null && lst.Count > 0)
        {
            conbination = new MahjongGroup(type, subType, lst, seat.Pos);
            conbination.Sort();
        }

        if (conbination != null)
        {
            for (int w = 0; w < Room.SeatList.Count; ++w)
            {
                Seat temp = Room.SeatList[w];
                for (int i = 0; i < conbination.MahjongList.Count; ++i)
                {
                    for (int j = 0; j < temp.PokerList.Count; ++j)
                    {
                        if (conbination.MahjongList[i].index == temp.PokerList[j].index)
                        {
                            temp.PokerList.RemoveAt(j);
                            break;
                        }
                    }
                    if (temp.HitPoker != null)
                    {
                        if (conbination.MahjongList[i].index == temp.HitPoker.index)
                        {
                            temp.HitPoker = null;
                        }
                    }
                    for (int j = 0; j < temp.DeskTopPoker.Count; ++j)
                    {
                        if (conbination.MahjongList[i].index == temp.DeskTopPoker[j].index)
                        {
                            temp.DeskTopPoker.RemoveAt(j);
                            break;
                        }
                    }
                    for (int j = temp.UsedPokerList.Count - 1; j >= 0; --j)
                    {
                        for (int k = 0; k < temp.UsedPokerList[j].MahjongList.Count; ++k)
                        {
                            if (conbination.MahjongList[i].index == temp.UsedPokerList[j].MahjongList[k].index)
                            {
                                temp.UsedPokerList[j].MahjongList.RemoveAt(k);
                                temp.UsedPokerList[j].CombinationType = OperationType.Peng;
                                break;
                            }
                        }
                        if (temp.UsedPokerList[j].MahjongList.Count == 0)
                        {
                            temp.UsedPokerList.RemoveAt(j);
                        }
                    }
                }
            }


            if (conbination != null)
            {
                seat.UsedPokerList.Add(conbination);
            }

            if (seat.HitPoker != null)
            {
                seat.PokerList.Add(seat.HitPoker);
                seat.HitPoker = null;
            }
        }
    }

    public void Pass(int playerId)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;

        Room.AskPokerGroup = null;
        if (seat.HitPoker != null) seat.Status = SeatStatus.PlayPoker;
        else seat.Status = SeatStatus.Wait;
    }

    public void Hu(int playerId, bool isZimo, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.isWiner = true;
        seat.Status = SeatStatus.Finish;
        if (mahjong != null)
        {
            seat.HitPoker = mahjong;

            for (int i = 0; i < Room.SeatList.Count; ++i)
            {
                bool isBreak = false;
                for (int j = 0; j < Room.SeatList[i].DeskTopPoker.Count; ++j)
                {
                    if (Room.SeatList[i].DeskTopPoker[j].index == seat.HitPoker.index)
                    {
                        Room.SeatList[i].DeskTopPoker.RemoveAt(j);
                        isBreak = true;
                        break;
                    }
                }
                if (isBreak) break;

                for (int j = 0; j < Room.SeatList[i].UsedPokerList.Count; ++j)
                {
                    for (int k = 0; k < Room.SeatList[i].UsedPokerList[j].MahjongList.Count; ++k)
                    {
                        if (Room.SeatList[i].UsedPokerList[j].MahjongList[k].index == seat.HitPoker.index)
                        {
                            Room.SeatList[i].UsedPokerList[j].MahjongList.RemoveAt(k);
                            if (Room.SeatList[i].UsedPokerList[j].CombinationType == OperationType.Gang)
                            {
                                Room.SeatList[i].UsedPokerList[j].CombinationType = OperationType.Peng;
                                isBreak = true;
                                break;
                            }
                        }
                    }
                    if (isBreak) break;
                }
                if (isBreak) break;
            }
        }
    }

    public void HandSort(int playerId)
    {
        HandSort(GetSeatByPlayerId(playerId));
    }

    public void HandSort(Seat seat)
    {
        if (seat == null) return;

        if (seat.PokerList.Count % 3 == 0 && seat.HitPoker != null)
        {
            seat.PokerList.Add(seat.HitPoker);
            seat.HitPoker = null;
        }

        MahjongHelper.SimpleSort(seat.PokerList);

        if (seat.Status == SeatStatus.PlayPoker && seat.HitPoker == null)
        {
            Mahjong lastPoker = seat.PokerList[seat.PokerList.Count - 1];
            seat.HitPoker = lastPoker;
            seat.PokerList.Remove(lastPoker);
        }
    }

    public void Settle(Room room)
    {
        if (Room == null) return;
        Room.RoomStatus = RoomStatus.Waiting;
        Room.isOver = room.isOver;
        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            Seat tempSeat = room.SeatList[i];
            Seat seat = GetSeatBySeatId(tempSeat.Pos);
            seat.Settle = tempSeat.Settle;
            seat.Gold = tempSeat.Gold;
            seat.isLoser = tempSeat.isLoser;
            seat.isWiner = tempSeat.isWiner;
            seat.UsedPokerList.Clear();
            seat.UsedPokerList.AddRange(tempSeat.UsedPokerList);
            seat.PokerList.Clear();
            seat.PokerList.AddRange(tempSeat.PokerList);
            seat.HitPoker = tempSeat.HitPoker;
            seat.incomesDesc = tempSeat.incomesDesc;
            HandSort(seat);
        }

        bool isZimo = true;
        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            if (Room.SeatList[i].isLoser) isZimo = false;
        }

        if (isZimo)
        {
            for (int i = 0; i < Room.SeatList.Count; ++i)
            {
                if (Room.SeatList[i].isWiner)
                {
                    Room.SeatList[i].isZiMo = true;
                }
            }
        }


        int seatCount = Room.SeatList.Count;

        int bankerPos = Room.BankerPos;
        List<int> winnerPos = new List<int>();
        for (int i = 0; i < seatCount; ++i)
        {
            if (Room.SeatList[i].isWiner)
            {
                winnerPos.Add(Room.SeatList[i].Pos);
            }
        }

        List<int> loserPos = new List<int>();
        for (int i = 0; i < seatCount; ++i)
        {
            if (Room.SeatList[i].isLoser || (!Room.SeatList[i].isWiner && isZimo))
            {
                loserPos.Add(Room.SeatList[i].Pos);
            }
        }

        List<int> winnerPrizeSeeds = new List<int>();
        for (int i = 0; i < winnerPos.Count; ++i)
        {
            int prizeSeed = ((winnerPos[i] + seatCount) - bankerPos + 1) % seatCount;
            winnerPrizeSeeds.Add(prizeSeed);
        }

        List<int> loserPrizeSeeds = new List<int>();
        for (int i = 0; i < loserPos.Count; ++i)
        {
            int prizeSeed = ((loserPos[i] + seatCount) - bankerPos + 1) % seatCount;
            loserPrizeSeeds.Add(prizeSeed);
        }
    }

    public void ReduceOverplusPoker(int reduce)
    {
        if (Room == null) return;

        Room.PokerAmount -= reduce;
    }

    public void SetStatus(RoomStatus status)
    {
        Room.RoomStatus = status;

        if (Room.RoomStatus == RoomStatus.Begin)
        {
            for (int i = 0; i < Room.SeatList.Count; ++i)
            {
                if (Room.SeatList[i].HitPoker != null)
                {
                    Room.SeatList[i].Status = SeatStatus.PlayPoker;
                }
                else
                {
                    Room.SeatList[i].Status = SeatStatus.Wait;
                }
            }
        }
    }

    public void GameOver(Room room)
    {
        if (room == null) return;
        if (Room == null) return;
        Room.isOver = true;
    }

    public void CheckTing()
    {
        if (Room == null) return;
        if (Room.PlayerSeat == null) return;

        CheckTingByHand(Room.PlayerSeat);
        CheckAllTing(Room.PlayerSeat);
        CalculatePokerCount(true);
    }

    private void CheckTingByHand(Seat seat)
    {
        seat.TingList.Clear();
        List<Mahjong> hand = new List<Mahjong>(seat.PokerList);
        if (hand.Count % 3 != 1) return;

        bool isExistsUniversal = false;
        for (int i = 0; i < seat.UniversalList.Count; ++i)
        {
            Mahjong uni = seat.UniversalList[i];
            for (int j = 0; j < hand.Count; ++j)
            {
                Mahjong h = hand[j];
                if (uni.color == h.color && uni.size == h.size)
                {
                    isExistsUniversal = true;
                    break;
                }
            }
            if (isExistsUniversal) break;
        }
    }

    private List<List<MahjongHelper.CardCombination>> GetTingCombination(List<Mahjong> pokers, List<MahjongGroup> usedPoker, List<Mahjong> universal)
    {
        List<Mahjong> ting = new List<Mahjong>(pokers);
        List<List<MahjongHelper.CardCombination>> lst = MahjongHelper.CheckTing(ting, usedPoker, Room.PlayerSeat.UniversalList);
        return lst;
    }

    private List<Mahjong> GetLackPoker(List<Mahjong> pokers, List<MahjongGroup> usedPoker, List<Mahjong> universal)
    {
        List<List<MahjongHelper.CardCombination>> lst = GetTingCombination(pokers, usedPoker, universal);
        return GetLackPoker(lst);
    }

    private List<Mahjong> GetLackPoker(List<List<MahjongHelper.CardCombination>> lst)
    {
        List<Mahjong> lackPokers = new List<Mahjong>();
        if (lst == null || lst.Count == 0) return lackPokers;
        for (int j = 0; j < lst.Count; ++j)
        {
            for (int k = 0; k < lst[j].Count; ++k)
            {
                List<Mahjong> lackCards = lst[j][k].LackCardIds;
                if (lackCards != null)
                {
                    for (int l = 0; l < lackCards.Count; ++l)
                    {
                        bool isExists = false;
                        for (int m = 0; m < lackPokers.Count; ++m)
                        {
                            if (lackCards[l].color == lackPokers[m].color && lackCards[l].size == lackPokers[m].size)
                            {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists)
                        {
                            lackPokers.Add(lackCards[l]);
                        }
                    }
                }
            }
        }
        MahjongHelper.SimpleSort(lackPokers);
        return lackPokers;
    }

    private void CheckAllTing(Seat seat)
    {
        seat.TingDic.Clear();
        List<Mahjong> hand = new List<Mahjong>(seat.PokerList);
        if (seat.HitPoker != null)
        {
            hand.Add(seat.HitPoker);
        }
        if (hand.Count % 3 != 2)
        {
            return;
        }
        Dictionary<Mahjong, List<Mahjong>> dic = CheckAllTing(hand, seat.UsedPokerList);
        if (dic != null)
        {
            foreach (var pair in dic)
            {
                seat.TingDic.Add(pair.Key, pair.Value);
            }
        }
    }

    private Dictionary<Mahjong, List<Mahjong>> CheckAllTing(List<Mahjong> mahjong, List<MahjongGroup> usedPoker)
    {
        Dictionary<Mahjong, List<Mahjong>> ret = new Dictionary<Mahjong, List<Mahjong>>();

        List<Mahjong> hand = new List<Mahjong>(mahjong);

        if (hand.Count < 2) return null;
        List<Mahjong> overplusPoker = new List<Mahjong>();
        for (int q = 0; q < hand.Count; ++q)
        {
            overplusPoker.Clear();
            Mahjong lack = hand[q];
            overplusPoker.AddRange(hand);
            overplusPoker.Remove(lack);

            bool isExistsUniversal = false;
            for (int i = 0; i < Room.PlayerSeat.UniversalList.Count; ++i)
            {
                Mahjong uni = Room.PlayerSeat.UniversalList[i];
                for (int j = 0; j < hand.Count; ++j)
                {
                    Mahjong h = hand[j];
                    if (uni.color == h.color && uni.size == h.size)
                    {
                        isExistsUniversal = true;
                        break;
                    }
                }
                if (isExistsUniversal) break;
            }

            List<Mahjong> lackPokers = GetLackPoker(overplusPoker, usedPoker, Room.PlayerSeat.UniversalList);
            if (lackPokers != null && lackPokers.Count > 0)
            {
                ret.Add(lack, lackPokers);
            }
        }
        return ret;
    }

    public List<Mahjong> GetHu(Mahjong mahjong)
    {
        if (Room == null) return null;
        if (Room.PlayerSeat == null) return null;
        if (!Room.PlayerSeat.TingDic.ContainsKey(mahjong)) return null;

        return Room.PlayerSeat.TingDic[mahjong];
    }

    public Dictionary<Mahjong, List<Mahjong>> GetAllTing()
    {
        if (Room == null) return null;
        if (Room.PlayerSeat == null) return null;
        return Room.PlayerSeat.TingDic;
    }

    public Seat GetSeatByPlayerId(int playerId)
    {
        if (Room == null) return null;
        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            if (Room.SeatList[i].PlayerId == playerId)
            {
                return Room.SeatList[i];
            }
        }
        return null;
    }

    public Seat GetSeatBySeatId(int seatId)
    {
        if (Room == null) return null;
        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            if (Room.SeatList[i].Pos == seatId)
            {
                return Room.SeatList[i];
            }
        }
        return null;
    }

    public Seat GetSeatBySeatIndex(int seatIndex)
    {
        if (Room == null) return null;
        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            if (Room.SeatList[i].Index == seatIndex)
            {
                return Room.SeatList[i];
            }
        }
        return null;
    }
}
