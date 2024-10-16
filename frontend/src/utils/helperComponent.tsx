import { CircularProgress, Stack } from '@mui/material'

const Loader = () => {
  return (
    <Stack height={'100vh'} justifyContent={'center'}>
      <CircularProgress
        sx={{ alignSelf: 'center', justifySelf: 'center' }}
      ></CircularProgress>
    </Stack>
  )
}

export default Loader
