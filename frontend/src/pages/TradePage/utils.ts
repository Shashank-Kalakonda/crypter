import axios from 'axios'
import { apiBase } from '../../utils/constants'
import numbro from 'numbro'

let globalUserId = 0

export interface InputDataItem {
  id: number
  acronym: string
  name: string
  iconUrl: string
  currentPrice: number
  marketCap: number
  circulatingSupply: number
  volume: number
  priceChangePercentage24h: number
}

export interface NameDetails {
  name: string
  currency: string
  imageSrc: string
}

export interface OutputDataItem {
  id: number
  Name: NameDetails
  Price: string
  MarketCap: string
  favorite: boolean
  Change: number
  volume: number
}

export interface PriceData {
  date: string
  price: number
}

export interface WatchlistData {
  id: number
  coinId: number
  userId: number
}

export const convertData = async (
  inputData: InputDataItem[],
  userId: number
): Promise<OutputDataItem[]> => {
  globalUserId = userId
  const watchlistData: WatchlistData[] = await getWatchlist(userId)
  const watchlistCoinIds = new Set()
  watchlistData.forEach((item) => watchlistCoinIds.add(item.coinId))

  const outputData: OutputDataItem[] = []
  for (const item of inputData) {
    const outputItem: OutputDataItem = {
      id: item.id,
      Name: {
        name: item.name,
        currency: item.acronym,
        imageSrc: item.iconUrl,
      },
      Price: `$${numbro(item.currentPrice).format({
        thousandSeparated: true,
      })}`,
      MarketCap:
        '$' + numbro(item.marketCap).format({ average: true }).toUpperCase(),
      Change:
        item.priceChangePercentage24h > 0
          ? '+' + item.priceChangePercentage24h.toFixed(2)
          : item.priceChangePercentage24h.toFixed(2),
      favorite: watchlistCoinIds.has(item.id),
      volume: item.volume,
    }

    outputData.push(outputItem)
  }

  return outputData
}

export const deleteFromWatchlist = async (watchlistId: number) => {
  await axios.delete(`${apiBase}/users/watchlist/${watchlistId}`, {
    headers: {
      Authorization: `Bearer ${sessionStorage.getItem('token')}`,
    },
  })
}

export const getWatchlist = async (userId: number) => {
  try {
    console.log('here')
    console.log(userId)
    const response = await axios.get(`${apiBase}/users/watchlist`, {
      params: {
        userId: userId,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    return response.data
  } catch (error) {
    console.error(error)
  }
}

export const getPriceData = async (coinId: number) => {
  try {
    const response = await axios.get(`${apiBase}/price`, {
      params: {
        coinId: coinId,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    console.log(response.data)
    return response.data[0].priceHistory
  } catch (error) {
    console.error(error)
  }
}

export const calculateChange = async (coinId: number) => {
  const priceHistory = await getPriceData(coinId)
  const sortedPriceHistory = priceHistory.sort(
    (a: PriceData, b: PriceData) => new Date(b.date) - new Date(a.date)
  )
  return (sortedPriceHistory[0].price - sortedPriceHistory[1].price) / 100
}

export const handleWatchlistUpdate = async (coinId: number) => {
  console.log('global', globalUserId)
  const watchlistData: WatchlistData[] = await getWatchlist(globalUserId)
  const existingData = watchlistData.find(
    (item) => item.userId === globalUserId && item.coinId === coinId
  )
  console.log(watchlistData)
  if (existingData) {
    try {
      await axios.delete(`${apiBase}/users/watchlist/`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
        data: {
          coinId: coinId,
          userId: globalUserId,
        },
      })
    } catch (e) {}
  } else {
    await axios.post(
      `${apiBase}/users/watchlist`,
      {
        coinId: coinId,
        userId: globalUserId,
      },
      {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      }
    )
  }
}
