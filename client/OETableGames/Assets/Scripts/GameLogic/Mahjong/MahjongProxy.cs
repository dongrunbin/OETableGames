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

    public void LeaveRoom(int playerId)
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

    public void CalculateMahjongCount(bool containHandMahjong)
    {
        if (Room == null) return;
        if (Room.SeatList == null) return;
        Room.MahjongCount.Clear();
        for (int i = 1; i < 8; ++i)
        {
            for (int j = 1; j < 10; ++j)
            {
                if (i == 5 && j > 3) break;
                if ((i == 4 || i > 5) && j > 4) break;
                int hash = string.Format("{0}_{1}", i, j).GetHashCode();
                Room.MahjongCount[hash] = 0;
            }
        }

        for (int i = 0; i < Room.SeatList.Count; ++i)
        {
            Seat seat = Room.SeatList[i];
            if (containHandMahjong)
            {
                for (int j = 0; j < seat.MahjongList.Count; ++j)
                {
                    int hash = seat.MahjongList[j].GetHashCode();
                    if (!Room.MahjongCount.ContainsKey(hash)) continue;
                    ++Room.MahjongCount[hash];
                }
                if (seat.HitMahjong != null)
                {
                    int hash = seat.HitMahjong.GetHashCode();
                    if (!Room.MahjongCount.ContainsKey(hash)) continue;
                    ++Room.MahjongCount[hash];
                }
            }
            for (int j = 0; j < seat.DeskTopMahjong.Count; ++j)
            {
                int hash = seat.DeskTopMahjong[j].GetHashCode();
                if (!Room.MahjongCount.ContainsKey(hash)) continue;
                ++Room.MahjongCount[hash];
            }
            for (int j = 0; j < seat.UsedMahjongGroups.Count; ++j)
            {
                for (int k = 0; k < seat.UsedMahjongGroups[j].MahjongList.Count; ++k)
                {
                    int hash = seat.UsedMahjongGroups[j].MahjongList[k].GetHashCode();
                    if (!Room.MahjongCount.ContainsKey(hash)) continue;
                    ++Room.MahjongCount[hash];
                }
            }
        }
    }

    public void Begin(Room room)
    {
        if (room == null) return;
        if (Room == null) return;
        Room.RoomStatus = RoomStatus.Gaming;
        Room.MahjongTotal = room.MahjongTotal;
        Room.currentLoop = room.currentLoop;
        Room.MahjongAmount = room.MahjongAmount;
        Room.FirstDice = room.FirstDice;
        Room.SecondDice = room.SecondDice;
        Room.PlayedTimes = 0;
        Room.MahjongCount.Clear();

        for (int i = 0; i < room.SeatList.Count; ++i)
        {
            Seat tempSeat = room.SeatList[i];
            Seat entity = GetSeatBySeatId(tempSeat.Pos);
            entity.Reset();
            entity.MahjongList.AddRange(tempSeat.MahjongList);
            entity.UniversalList.AddRange(tempSeat.UniversalList);
            entity.MahjongAmount = tempSeat.MahjongAmount;
            entity.IsBanker = tempSeat.IsBanker;
        }
    }

    public void Draw(int playerId, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;

        if (seat.HitMahjong != null)
        {
            seat.MahjongList.Add(seat.HitMahjong);
            seat.HitMahjong = mahjong;
        }
        else
        {
            seat.HitMahjong = mahjong;
        }

        seat.Status = SeatStatus.Discard;
    }

    public void Discard(int playerId, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        ++Room.PlayedTimes;
        seat.Status = SeatStatus.Wait;
        CalculateMahjongCount(false);
        ++seat.playedTimes;

        if (seat.HitMahjong != null)
        {
            if (seat.HitMahjong.index == mahjong.index)
            {
                seat.HitMahjong = null;
            }
            else
            {
                for (int i = 0; i < seat.MahjongList.Count; ++i)
                {
                    if (seat.MahjongList[i].index == mahjong.index)
                    {
                        seat.MahjongList.Remove(seat.MahjongList[i]);
                        break;
                    }
                }
                seat.MahjongList.Add(seat.HitMahjong);
                seat.HitMahjong = null;
            }
        }
        else
        {
            for (int i = 0; i < seat.MahjongList.Count; ++i)
            {
                if (seat.MahjongList[i].index == mahjong.index)
                {
                    seat.MahjongList.Remove(seat.MahjongList[i]);
                    break;
                }
            }
        }
        seat.DeskTopMahjong.Add(mahjong);
    }

    public void AskOperation(List<MahjongGroup> lst)
    {
        if (Room == null) return;
        if (Room.PlayerSeat == null) return;
        Room.AskMahjongGroup = lst;
        Room.PlayerSeat.Status = SeatStatus.Operate;
    }

    public void OperateMahjong(OperationType type, int playerId, int subType, List<Mahjong> lst)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.Status = SeatStatus.Discard;

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
                    for (int j = 0; j < temp.MahjongList.Count; ++j)
                    {
                        if (conbination.MahjongList[i].index == temp.MahjongList[j].index)
                        {
                            temp.MahjongList.RemoveAt(j);
                            break;
                        }
                    }
                    if (temp.HitMahjong != null)
                    {
                        if (conbination.MahjongList[i].index == temp.HitMahjong.index)
                        {
                            temp.HitMahjong = null;
                        }
                    }
                    for (int j = 0; j < temp.DeskTopMahjong.Count; ++j)
                    {
                        if (conbination.MahjongList[i].index == temp.DeskTopMahjong[j].index)
                        {
                            temp.DeskTopMahjong.RemoveAt(j);
                            break;
                        }
                    }
                    for (int j = temp.UsedMahjongGroups.Count - 1; j >= 0; --j)
                    {
                        for (int k = 0; k < temp.UsedMahjongGroups[j].MahjongList.Count; ++k)
                        {
                            if (conbination.MahjongList[i].index == temp.UsedMahjongGroups[j].MahjongList[k].index)
                            {
                                temp.UsedMahjongGroups[j].MahjongList.RemoveAt(k);
                                temp.UsedMahjongGroups[j].CombinationType = OperationType.Pong;
                                break;
                            }
                        }
                        if (temp.UsedMahjongGroups[j].MahjongList.Count == 0)
                        {
                            temp.UsedMahjongGroups.RemoveAt(j);
                        }
                    }
                }
            }


            if (conbination != null)
            {
                seat.UsedMahjongGroups.Add(conbination);
            }

            if (seat.HitMahjong != null)
            {
                seat.MahjongList.Add(seat.HitMahjong);
                seat.HitMahjong = null;
            }
        }
    }

    public void Pass()
    {
        Seat seat = Room.PlayerSeat;
        if (seat == null) return;

        Room.AskMahjongGroup = null;
        if (seat.HitMahjong != null) seat.Status = SeatStatus.Discard;
        else seat.Status = SeatStatus.Wait;
    }

    public void Hu(int playerId, bool isZimo, Mahjong mahjong)
    {
        Seat seat = GetSeatByPlayerId(playerId);
        if (seat == null) return;
        seat.isWiner = true;
        if (mahjong != null)
        {
            seat.HitMahjong = mahjong;

            for (int i = 0; i < Room.SeatList.Count; ++i)
            {
                bool isBreak = false;
                for (int j = 0; j < Room.SeatList[i].DeskTopMahjong.Count; ++j)
                {
                    if (Room.SeatList[i].DeskTopMahjong[j].index == seat.HitMahjong.index)
                    {
                        Room.SeatList[i].DeskTopMahjong.RemoveAt(j);
                        isBreak = true;
                        break;
                    }
                }
                if (isBreak) break;

                for (int j = 0; j < Room.SeatList[i].UsedMahjongGroups.Count; ++j)
                {
                    for (int k = 0; k < Room.SeatList[i].UsedMahjongGroups[j].MahjongList.Count; ++k)
                    {
                        if (Room.SeatList[i].UsedMahjongGroups[j].MahjongList[k].index == seat.HitMahjong.index)
                        {
                            Room.SeatList[i].UsedMahjongGroups[j].MahjongList.RemoveAt(k);
                            if (Room.SeatList[i].UsedMahjongGroups[j].CombinationType == OperationType.Kong)
                            {
                                Room.SeatList[i].UsedMahjongGroups[j].CombinationType = OperationType.Pong;
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

        if (seat.MahjongList.Count % 3 == 0 && seat.HitMahjong != null)
        {
            seat.MahjongList.Add(seat.HitMahjong);
            seat.HitMahjong = null;
        }

        MahjongHelper.SimpleSort(seat.MahjongList);

        if (seat.Status == SeatStatus.Discard && seat.HitMahjong == null)
        {
            Mahjong lastMahjong = seat.MahjongList[seat.MahjongList.Count - 1];
            seat.HitMahjong = lastMahjong;
            seat.MahjongList.Remove(lastMahjong);
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
            seat.UsedMahjongGroups.Clear();
            seat.UsedMahjongGroups.AddRange(tempSeat.UsedMahjongGroups);
            seat.MahjongList.Clear();
            seat.MahjongList.AddRange(tempSeat.MahjongList);
            seat.HitMahjong = tempSeat.HitMahjong;
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

    public void ReduceOverplusMahjong(int reduce)
    {
        if (Room == null) return;

        Room.MahjongAmount -= reduce;
    }

    public void Result(Room room)
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
        CalculateMahjongCount(true);
    }

    private void CheckTingByHand(Seat seat)
    {
        seat.TingList.Clear();
        List<Mahjong> hand = new List<Mahjong>(seat.MahjongList);
        if (hand.Count % 3 != 1) return;

        bool isExistsUniversal = false;
        for (int i = 0; i < seat.UniversalList.Count; ++i)
        {
            Mahjong uni = seat.UniversalList[i];
            for (int j = 0; j < hand.Count; ++j)
            {
                Mahjong h = hand[j];
                if (uni.color == h.color && uni.number == h.number)
                {
                    isExistsUniversal = true;
                    break;
                }
            }
            if (isExistsUniversal) break;
        }
    }

    private List<List<MahjongHelper.CardCombination>> GetTingCombination(List<Mahjong> mahjongs, List<MahjongGroup> usedMahjong, List<Mahjong> universal)
    {
        List<Mahjong> ting = new List<Mahjong>(mahjongs);
        List<List<MahjongHelper.CardCombination>> lst = MahjongHelper.CheckTing(ting, usedMahjong, Room.PlayerSeat.UniversalList);
        return lst;
    }

    private List<Mahjong> GetLackMahjong(List<Mahjong> mahjongs, List<MahjongGroup> usedMahjong, List<Mahjong> universal)
    {
        List<List<MahjongHelper.CardCombination>> lst = GetTingCombination(mahjongs, usedMahjong, universal);
        return GetLackMahjong(lst);
    }

    private List<Mahjong> GetLackMahjong(List<List<MahjongHelper.CardCombination>> lst)
    {
        List<Mahjong> lackMahjongs = new List<Mahjong>();
        if (lst == null || lst.Count == 0) return lackMahjongs;
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
                        for (int m = 0; m < lackMahjongs.Count; ++m)
                        {
                            if (lackCards[l].color == lackMahjongs[m].color && lackCards[l].number == lackMahjongs[m].number)
                            {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists)
                        {
                            lackMahjongs.Add(lackCards[l]);
                        }
                    }
                }
            }
        }
        MahjongHelper.SimpleSort(lackMahjongs);
        return lackMahjongs;
    }

    private void CheckAllTing(Seat seat)
    {
        seat.TingDic.Clear();
        List<Mahjong> hand = new List<Mahjong>(seat.MahjongList);
        if (seat.HitMahjong != null)
        {
            hand.Add(seat.HitMahjong);
        }
        if (hand.Count % 3 != 2)
        {
            return;
        }
        Dictionary<Mahjong, List<Mahjong>> dic = CheckAllTing(hand, seat.UsedMahjongGroups);
        if (dic != null)
        {
            foreach (var pair in dic)
            {
                seat.TingDic.Add(pair.Key, pair.Value);
            }
        }
    }

    private Dictionary<Mahjong, List<Mahjong>> CheckAllTing(List<Mahjong> mahjong, List<MahjongGroup> usedMahjong)
    {
        Dictionary<Mahjong, List<Mahjong>> ret = new Dictionary<Mahjong, List<Mahjong>>();

        List<Mahjong> hand = new List<Mahjong>(mahjong);

        if (hand.Count < 2) return null;
        List<Mahjong> overplusMahjong = new List<Mahjong>();
        for (int q = 0; q < hand.Count; ++q)
        {
            overplusMahjong.Clear();
            Mahjong lack = hand[q];
            overplusMahjong.AddRange(hand);
            overplusMahjong.Remove(lack);

            bool isExistsUniversal = false;
            for (int i = 0; i < Room.PlayerSeat.UniversalList.Count; ++i)
            {
                Mahjong uni = Room.PlayerSeat.UniversalList[i];
                for (int j = 0; j < hand.Count; ++j)
                {
                    Mahjong h = hand[j];
                    if (uni.color == h.color && uni.number == h.number)
                    {
                        isExistsUniversal = true;
                        break;
                    }
                }
                if (isExistsUniversal) break;
            }

            List<Mahjong> lackMahjongs = GetLackMahjong(overplusMahjong, usedMahjong, Room.PlayerSeat.UniversalList);
            if (lackMahjongs != null && lackMahjongs.Count > 0)
            {
                ret.Add(lack, lackMahjongs);
            }
        }
        return ret;
    }

    public List<List<Mahjong>> GetChi(Mahjong mahjong)
    {
        List<Mahjong> handList = new List<Mahjong>(Room.PlayerSeat.MahjongList);

        List<Mahjong> universal = Room.PlayerSeat.UniversalList;

        List<List<Mahjong>> chi = new List<List<Mahjong>>();

        if (mahjong.color < 4)
        {
            //检查第三张
            if (mahjong.number >= 3)
            {
                List<Mahjong> lst = new List<Mahjong>();
                Mahjong first = null;
                Mahjong second = null;
                for (int i = 0; i < handList.Count; ++i)
                {
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number - 2)
                    {
                        first = handList[i];
                    }
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number - 1)
                    {
                        second = handList[i];
                    }
                    if (first != null && second != null)
                    {
                        lst.Add(first);
                        lst.Add(second);
                        lst.Add(mahjong);
                        chi.Add(lst);
                        break;
                    }
                }
            }
            //检查第二张
            if (mahjong.number > 1 && mahjong.number < 9)
            {
                List<Mahjong> lst = new List<Mahjong>();
                Mahjong first = null;
                Mahjong third = null;
                for (int i = 0; i < handList.Count; ++i)
                {
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number - 1)
                    {
                        first = handList[i];
                    }
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number + 1)
                    {
                        third = handList[i];
                    }
                    if (first != null && third != null)
                    {
                        lst.Add(first);
                        lst.Add(mahjong);
                        lst.Add(third);

                        chi.Add(lst);
                        break;
                    }
                }
            }
            //检查第一张
            if (mahjong.number <= 7)
            {
                List<Mahjong> lst = new List<Mahjong>();
                Mahjong second = null;
                Mahjong third = null;
                for (int i = 0; i < handList.Count; ++i)
                {
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number + 1)
                    {
                        second = handList[i];
                    }
                    if (handList[i].color == mahjong.color && handList[i].number == mahjong.number + 2)
                    {
                        third = handList[i];
                    }
                    if (second != null && third != null)
                    {
                        lst.Add(mahjong);
                        lst.Add(second);
                        lst.Add(third);

                        chi.Add(lst);
                        break;
                    }
                }
            }
        }
        return chi;
    }

    public List<Mahjong> GetPeng(Mahjong mahjong)
    {
        int sameCount = 1;
        List<Mahjong> lst = new List<Mahjong>();
        List<Mahjong> handList = new List<Mahjong>(Room.PlayerSeat.MahjongList);
        for (int i = 0; i < handList.Count; ++i)
        {
            if (handList[i].color == mahjong.color && handList[i].number == mahjong.number)
            {
                lst.Add(handList[i]);
                ++sameCount;
                if (sameCount == 3)
                {
                    lst.Add(mahjong);
                    return lst;
                }
            }
        }
        return null;
    }

    public List<List<Mahjong>> GetAnGang()
    {
        List<List<Mahjong>> lst = new List<List<Mahjong>>();
        if (Room.PlayerSeat == null) return lst;
        List<Mahjong> handList = new List<Mahjong>(Room.PlayerSeat.MahjongList);
        Mahjong hitMahjong = Room.PlayerSeat.HitMahjong;
        if (hitMahjong != null)
        {
            handList.Add(hitMahjong);
        }
        for (int i = 0; i < handList.Count; ++i)
        {
            List<Mahjong> combination = new List<Mahjong>();
            Mahjong mahjong = handList[i];
            combination.Add(mahjong);
            int sameCount = 1;
            for (int j = i + 1; j < handList.Count; ++j)
            {
                if (mahjong.color == handList[j].color && mahjong.number == handList[j].number)
                {
                    combination.Add(handList[j]);
                    ++sameCount;
                    if (sameCount == 4)
                    {
                        lst.Add(combination);
                        break;
                    }
                }
            }
        }
        return lst;
    }

    public List<Mahjong> GetBuGang()
    {
        List<Mahjong> lst = new List<Mahjong>();

        List<Mahjong> handList = new List<Mahjong>();
        handList.AddRange(Room.PlayerSeat.MahjongList);

        Mahjong hitMahjong = Room.PlayerSeat.HitMahjong;
        if (hitMahjong != null)
        {
            handList.Add(hitMahjong);
        }
        List<MahjongGroup> pengList = Room.PlayerSeat.UsedMahjongGroups;
        for (int i = 0; i < pengList.Count; ++i)
        {
            if (pengList[i].CombinationType == OperationType.Pong)
            {
                for (int j = 0; j < handList.Count; ++j)
                {
                    if (handList[j].color == pengList[i].MahjongList[0].color && handList[j].number == pengList[i].MahjongList[0].number)
                    {
                        lst.Add(handList[j]);
                    }
                }
            }
        }
        return lst;
    }

    public List<Mahjong> GetBuGang(Mahjong mahjong)
    {
        List<Mahjong> lst = new List<Mahjong>();
        List<Mahjong> handList = new List<Mahjong>(Room.PlayerSeat.MahjongList);

        Mahjong hitMahjong = Room.PlayerSeat.HitMahjong;
        if (hitMahjong != null)
        {
            handList.Add(hitMahjong);
        }
        List<MahjongGroup> pengList = Room.PlayerSeat.UsedMahjongGroups;
        for (int i = 0; i < pengList.Count; ++i)
        {
            if (pengList[i].CombinationType == OperationType.Pong)
            {
                if (mahjong.color == pengList[i].MahjongList[0].color && mahjong.number == pengList[i].MahjongList[0].number)
                {
                    lst.Add(mahjong);
                }
            }
        }
        return lst;
    }

    public List<Mahjong> GetMingGang(Mahjong mahjong)
    {
        if (mahjong == null) return null;
        if (Room.PlayerSeat == null) return null;
        List<Mahjong> lst = new List<Mahjong>();

        List<Mahjong> handList = new List<Mahjong>(Room.PlayerSeat.MahjongList);
        int sameCount = 1;
        for (int i = 0; i < handList.Count; ++i)
        {
            if (handList[i].color == mahjong.color && handList[i].number == mahjong.number)
            {
                lst.Add(handList[i]);
                ++sameCount;
                if (sameCount == 4)
                {
                    lst.Add(mahjong);
                    break;
                }
            }
        }
        if (lst.Count < 4) lst.Clear();

        return lst;
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
